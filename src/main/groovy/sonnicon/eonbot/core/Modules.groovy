package sonnicon.eonbot.core

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

    static ModuleBase load(String name = "startup") {
        String filename = name + ".groovy"
        if (file(name, FileExtension.script).exists()) {
            if (loadedModules.containsKey(name)) {
                unload(name)
            }
            binding.setProperty("name", name)
            ModuleBase mod = groovyScriptEngine.run(filename, binding) as ModuleBase
            loadedModules.put(name, mod)
            File commands = file(name, FileExtension.command)
            if(commands.exists())
                CommandRegistry.loadYaml(commands, name)
            mod.load()
            mod
        } else {
            null
        }
    }

    static protected File file(String name, FileExtension extension){
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

    enum FileExtension{
        script("groovy"),
        command("command.yaml")

        String extension
        FileExtension(String ext){
            this.extension = "." + ext
        }
    }


    static abstract class ModuleBase extends Script {

        void load() {}

        void unload() {}

        @Override
        Object run() {
            this
        }
    }
}
