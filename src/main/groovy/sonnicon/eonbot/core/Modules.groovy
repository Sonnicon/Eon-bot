package sonnicon.eonbot.core

import sonnicon.eonbot.command.CommandRegistry
import sonnicon.eonbot.type.ExecutorFunc
import sonnicon.eonbot.type.ModuleBase
import sonnicon.eonbot.type.ModuleMeta

import java.lang.reflect.Method

class Modules {
    static protected GroovyScriptEngine groovyScriptEngine

    static Map<String, ModuleMeta> moduleMetas = [:]
    // Map<context, Map<name, ModuleBase>>
    static Map<String, Map<String, ModuleBase>> moduleInstances = [:] as Map<String, Map<String, ModuleBase>>

    static {
        groovyScriptEngine = new GroovyScriptEngine(FileIO.FileType.modules.file.getAbsolutePath())
        groovyScriptEngine.config.setScriptBaseClass(ModuleBase.getClass().name)
        loadModule("startup")
    }

    static loadModule(String name, String context = null, boolean force = false) {
        if (loadClass(name, force)) {
            loadInstance(name, context, force)
        }
    }

    static ModuleBase loadInstance(String name, String context = null, boolean force = false) {
        ModuleMeta meta = moduleMetas.get(name)
        if (!moduleMetas.containsKey(name)) {
            println "Could not find moduleclass for module '$name'; Aborting."
            return null
        }

        if (meta.isShared && context != null) {
            if (!force) {
                println "Not loading shared module '$name' with context '$context'."
                return null
            }
            context = null
        }

        Map<String, ModuleBase> map = moduleInstances.get(context)
        if (map == null) {
            map = [:]
            moduleInstances.put(context, map)
        } else if (map?.containsKey(name)) {
            if (moduleMetas.get(name).moduleBase == map.get(name).class && !force) {
                println "Module '$name' for '$context' is already loaded and up to date, not forcing reload."
                return null
            } else {
                unloadInstance(name, context)
                moduleInstances.putIfAbsent(context, [:])
                map = moduleInstances.get(context)
            }
        }

        meta.dependencies?.each { String it ->
            if (!loadInstance(it, context)) {
                println "Could not load dependency '$it' for module '$name'; Aborting."
                return null
            }
        }

        ModuleBase instance = meta.moduleBase.newInstance()
        instance.metaClass.context = context
        map.put(name, instance)

        // load commands
        if (meta?.commands != null) {
            // Get all methods with ExecutorFunc into a map with the key and a reference to method
            Map<String, Closure<Boolean>> executorMap = meta.moduleBase.getDeclaredMethods().findAll {
                it.getAnnotation(ExecutorFunc)
            }.collectEntries { Method it ->
                [it.getAnnotation(ExecutorFunc).value(), instance.&"$it.name"]
            }

            CommandRegistry.loadMap(meta.commands, context, name, executorMap)
        }

        instance.load()
        println "Loaded module '$name'${context == null ? "." : " with context '$context'."}"
        instance
    }

    static ModuleMeta loadClass(String name, boolean force = false) {
        File fileScript = file(name, FileExtension.script)
        File fileMeta = file(name, FileExtension.meta)
        long lastChange = Math.max(fileMeta.lastModified(), fileScript.lastModified())
        if (moduleMetas.containsKey(name)) {
            ModuleMeta current = moduleMetas.get(name)
            if (current.lastChange >= lastChange && !force) {
                println "Not loading moduleclass '$name'; already loaded and up to date."
                return current
            }
        }

        ModuleMeta meta
        if (fileMeta.exists()) {
            meta = FileIO.yamlSlurper.parse(fileMeta) as ModuleMeta
        } else {
            meta = new ModuleMeta()
        }
        meta.lastChange = lastChange

        meta.dependencies?.each { String it ->
            if (!loadClass(it, force)) {
                println "Could not load dependency '$it' for moduleclass '$name'."
                return null
            }
        }

        meta.moduleBase = groovyScriptEngine.loadScriptByName(name + FileExtension.script.extension)
        meta.moduleBase.metaClass.static.name = name
        moduleMetas.put(name, meta)
        meta
    }

    static protected File file(String name, FileExtension extension) {
        return new File(FileIO.FileType.modules.file, name + extension.extension)
    }

    static boolean unloadModule(String name) {
        !moduleInstances.find {
            if (it.value.containsKey(name)) {
                !unloadInstance(name, it.key)
            } else {
                false
            }
        } && unloadClass(name)
    }

    static boolean unloadInstance(String name, String context = null) {
        Map<String, ModuleBase> instances = moduleInstances.get(context)
        ModuleBase mod = instances?.get(name)
        if (mod) {
            mod.unload()
            CommandRegistry.unloadModule(name, context)
            instances.remove(name)
            if (instances.size() == 0) {
                moduleInstances.remove(context)
            }
        } else {
            println "Could not unload module '$name' for context '$context'; Not found."
        }
        mod
    }

    static boolean unloadClass(String name) {
        ModuleMeta c = moduleMetas.get(name)
        if (c) {
            moduleMetas.remove(name)
        } else {
            println "Could not unload moduleclass '$c'; Not found."
        }
        c
    }

    enum FileExtension {
        script("groovy"),
        meta("yaml")

        String extension

        FileExtension(String ext) {
            this.extension = "." + ext
        }
    }
}
