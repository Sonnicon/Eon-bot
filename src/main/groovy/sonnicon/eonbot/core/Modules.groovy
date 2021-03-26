package sonnicon.eonbot.core

import static sonnicon.eonbot.core.FileIO.FileType.modules

class Modules {
    static protected GroovyScriptEngine groovyScriptEngine
    static protected Binding binding

    static {
        groovyScriptEngine = new GroovyScriptEngine(modules.file.getAbsolutePath())
        groovyScriptEngine.config.setScriptBaseClass(ModuleBase.getClass().name)

        File startup = new File(modules.file, "startup.groovy")
        if(startup.exists()){
            groovyScriptEngine.run("startup.groovy", binding)
        }
    }

    static class ModuleBase extends Script {
        @Override
        Object run() {
            null
        }
    }
}
