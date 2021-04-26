package sonnicon.eonbot.core


import sonnicon.eonbot.command.CmdNode
import sonnicon.eonbot.command.CommandRegistry

class Modules {
    static protected GroovyScriptEngine groovyScriptEngine
    static protected Binding binding = new Binding()

    static Map<String, ModuleBase> loadedModules = [:]

    static {
        groovyScriptEngine = new GroovyScriptEngine(FileIO.FileType.modules.file.getAbsolutePath())
        groovyScriptEngine.config.setScriptBaseClass(ModuleBase.getClass().name)
        load()
    }

    static ModuleBase load(String name = "startup", boolean force = false) {
        String filename = name + ".groovy"
        if (file(name, FileExtension.script).exists()) {
            // unload existing
            if (loadedModules.containsKey(name)) {
                if (!force) {
                    println "Module $name already loaded, not forcing reload."
                    return loadedModules.get(name)
                }
                unload(name)
            }
            // load dependencies
            File file = file(name, FileExtension.yaml)
            Map<String, List> yaml
            if (file.exists()) {
                yaml = FileIO.yamlSlurper.parse(file) as Map<String, List>
            }
            if (yaml && yaml.containsKey("dependencies")) {
                yaml.get("dependencies").each {
                    if (!load(it as String)) {
                        println "Could not load dependency '$it' for module '$name'; Aborting."
                        return null
                    }
                }
            }
            // load script
            binding.setProperty("name", name)
            ModuleBase mod = groovyScriptEngine.run(filename, binding) as ModuleBase
            loadedModules.put(name, mod)
            // load commands
            if (yaml && yaml.containsKey("commands"))
                CommandRegistry.loadMap(yaml.get("commands") as Map<String, CmdNode>, name, mod.getExecutorMap())
            mod.load()
            println "Loaded module " + name
            mod
        } else {
            null
        }
    }

    static protected File file(String name, FileExtension extension) {
        return new File(FileIO.FileType.modules.file, name + extension.extension)
    }

    static ModuleBase unload(String name) {
        ModuleBase mod = loadedModules.get(name)
        if (mod != null) {
            mod.unload()
            CommandRegistry.unloadModule(name)
            loadedModules.remove(name)
        } else {
            null
        }
    }

    enum FileExtension {
        script("groovy"),
        yaml("yaml")

        String extension

        FileExtension(String ext) {
            this.extension = "." + ext
        }
    }


    static abstract class ModuleBase extends Script {

        void load() {}

        void unload() {}

        static Map<String, Closure> getExecutorMap() { null }

        @Override
        Object run() {
            this
        }
    }
}
