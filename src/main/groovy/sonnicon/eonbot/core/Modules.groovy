package sonnicon.eonbot.core

import sonnicon.eonbot.command.CommandRegistry
import sonnicon.eonbot.type.ExecutorFunc
import sonnicon.eonbot.type.ModuleBase
import sonnicon.eonbot.type.ModuleMeta

import java.lang.reflect.Method

/**
 * Class to facilitate loading, unloading, and storing of modules.
 */
class Modules {
    /**
     * GroovyScriptEngine configured for loading modules.
     */
    static protected GroovyScriptEngine groovyScriptEngine
    /**
     * Map between module name and metadata of the module.
     */
    static Map<String, ModuleMeta> moduleMetas = [:]
    /**
     * Map between contexts, and maps between module name and module objects,
     */
    static Map<String, Map<String, ModuleBase>> moduleInstances = [:] as Map<String, Map<String, ModuleBase>>

    static {
        // Init script engine
        groovyScriptEngine = new GroovyScriptEngine(FileIO.FileType.modules.file.getAbsolutePath())
        groovyScriptEngine.config.setScriptBaseClass(ModuleBase.getClass().name)
        // Load 'startup' module
        loadModule("startup")
    }

    /**
     * Try to load a module and initialize it for the given context.
     * @param name Name of the module
     * @param context Context for which module will be loaded
     * @param force Continue if newer already loaded
     * @return Instance of the loaded module, null if nothing loaded
     */
    static loadModule(String name, String context = null, boolean force = false) {
        if (loadClass(name, force)) {
            loadInstance(name, context, force)
        }
    }

    /**
     * Create and load an instance of a module for a context. Reloads if a newer class is available.
     * @param name Name of the module
     * @param context Context for which the module will be loaded, set to null on shared modules
     * @param force If true; load on context for shared modules, reload if same class already loaded
     * @return Instance of the loaded module, null if nothing loaded
     */
    static ModuleBase loadInstance(String name, String context = null, boolean force = false) {
        // Check if module exists in memory
        if (!moduleMetas.containsKey(name)) {
            println "Could not find moduleclass for module '$name'; Aborting."
            return null
        }
        ModuleMeta meta = moduleMetas.get(name)

        // Don't load shared modules on a context
        if (meta.isShared && context != null) {
            // Unless we force
            if (!force) {
                println "Fixing loading shared module '$name' from context '$context' to context 'null'."
                context = null
            }
        }


        Map<String, ModuleBase> map = moduleInstances.get(context)
        if (map == null) {
            // Context doesn't have a map yet
            map = [:]
            moduleInstances.put(context, map)
        } else if (map?.containsKey(name)) {
            if (moduleMetas.get(name).moduleBase == map.get(name).class && !force) {
                // Don't load if already loaded for that context
                println "Module '$name' for '$context' is already loaded and up to date, not forcing reload."
                return null
            } else {
                // If forcing and already loaded, we unload first
                unloadInstance(name, context)
                moduleInstances.putIfAbsent(context, [:])
                map = moduleInstances.get(context)
            }
        }

        // Load all dependencies
        meta.dependencies?.each { String it ->
            if (!loadInstance(it, context)) {
                println "Could not load dependency '$it' for module '$name'; Aborting."
                return null
            }
        }

        // Create new instance
        ModuleBase instance = meta.moduleBase.getDeclaredConstructor().newInstance()
        instance.metaClass.context = context
        map.put(name, instance)

        // Load commands
        if (meta?.commands != null) {
            // Get all methods with ExecutorFunc into a map with the key and a reference to method
            Map<String, Closure<Boolean>> executorMap = meta.moduleBase.getDeclaredMethods().findAll {
                it.getAnnotation(ExecutorFunc)
            }.collectEntries { Method it ->
                [it.getAnnotation(ExecutorFunc).value(), instance.&"$it.name"]
            }

            CommandRegistry.loadMap(meta.commands, context, name, executorMap)
        }

        // Module init
        instance.load(context)
        println "Loaded module '$name'${context == null ? "." : " with context '$context'."}"
        instance
    }

    /**
     * Load the script and metadata for a given module into memory.
     * @param name Name of module
     * @param force Continue if newer already loaded
     * @return Metadata of loaded module, including script object
     */
    static ModuleMeta loadClass(String name, boolean force = false) {
        File fileScript = file(name, FileExtension.script)
        File fileMeta = file(name, FileExtension.meta)
        // Don't load if newer already loaded, and not forcing
        long lastChange = Math.max(fileMeta.lastModified(), fileScript.lastModified())
        if (moduleMetas.containsKey(name)) {
            ModuleMeta current = moduleMetas.get(name)
            if (current.lastChange >= lastChange && !force) {
                println "Not loading moduleclass '$name'; already loaded and up to date."
                return current
            }
        }

        // Parse metadata
        ModuleMeta meta
        if (fileMeta.exists()) {
            meta = FileIO.yamlSlurper.parse(fileMeta) as ModuleMeta
        } else {
            meta = new ModuleMeta()
        }
        meta.lastChange = lastChange

        // Load all dependency module classes
        meta.dependencies?.each { String it ->
            if (!loadClass(it, force)) {
                println "Could not load dependency '$it' for moduleclass '$name'."
                return null
            }
        }

        // Load script and store
        meta.moduleBase = groovyScriptEngine.loadScriptByName(name + FileExtension.script.extension)
        meta.moduleBase.metaClass.static.name = name
        moduleMetas.put(name, meta)
        meta
    }

    /**
     * Sanitize module name, and get the file for a module name.
     * @param name Name of the module to get
     * @param extension Type of module file to get
     * @return File for the module
     */
    static protected File file(String name, FileExtension extension) {
        return new File(FileIO.FileType.modules.file, FileIO.sanitizeFilename(name) + extension.extension)
    }

    /**
     * Attempts to unload all instances of a module, and then the class. Stops if anything fails to unload.
     * @param name Name of module to unload
     * @return Whether everything was unloaded successfully
     */
    static boolean unloadModule(String name) {
        // todo safety for unloading modules which are dependents
        !moduleInstances.find {
            if (it.value.containsKey(name)) {
                !unloadInstance(name, it.key)
            } else {
                false
            }
        } && unloadClass(name)
    }

    /**
     * Unloads an instance for a context.
     * @param name Name of module to be unloaded
     * @param context Context for which module will be unloaded
     * @return Whether instance was unloaded
     */
    static boolean unloadInstance(String name, String context = null) {
        Map<String, ModuleBase> instances = moduleInstances.get(context)
        ModuleBase mod = instances?.get(name)
        if (mod) {
            // Instance exists
            mod.unload(context)
            CommandRegistry.unloadModule(name, context)
            instances.remove(name)
            if (instances.size() == 0) {
                moduleInstances.remove(context)
            }
        } else {
            // Instance doesn't exist
            println "Could not unload module '$name' for context '$context'; Not found."
        }
        mod
    }

    /**
     * Removes the class for specified module from map. Won't remove loaded instances.
     * @param name Name of module to class remove
     * @return Whether module was found and removed
     */
    static boolean unloadClass(String name) {
        ModuleMeta c = moduleMetas.get(name)
        if (c) {
            moduleMetas.remove(name)
        } else {
            println "Could not unload moduleclass '$c'; Not found."
        }
        c
    }

    /**
     * File extensions for modules
     */
    enum FileExtension {
        script("groovy"),
        meta("yaml")

        String extension

        FileExtension(String ext) {
            this.extension = "." + ext
        }
    }
}
