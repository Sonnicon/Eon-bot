package sonnicon.eonbot.core

import org.codehaus.groovy.control.CompilerConfiguration
import sonnicon.eonbot.Eonbot
import sonnicon.eonbot.type.ModuleScript
import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.util.Files

class Modules {
    static moduleMap = [:]
    static String moduleName = ""

    private static GroovyScriptEngine gse

    static init() {
        CompilerConfiguration config = new CompilerConfiguration()
        config.recompileGroovySource = false
        config.setScriptBaseClass(ModuleScript.name.toString())

        GroovyClassLoader gcl = new GroovyClassLoader(Eonbot.getClassLoader(), config)
        Files.modules.listFiles().each { file -> gcl.addURL(file.toURI().toURL()) }

        gse = new GroovyScriptEngine(Files.modules.path, gcl)
        gse.setConfig(config)
    }

    static boolean loadModule(String module) {
        return loadModule(module, "")
    }

    static boolean loadModule(String module, String args) {
        File file = Files.fileModule(module)
        if (!file.exists()) return false
        //reload cleanup
        unloadModule(module)

        moduleName = module
        gse.groovyClassLoader.addURL(file.toURI().toURL())
        Binding b = new Binding([arg: args, "moduleName": module])
        gse.run(file.name, b)
        true
    }

    static unloadModule(String module) {
        if (moduleMap.containsKey(module)) {
            (moduleMap.get(module) as ModuleScript).unload()
            moduleMap.remove(module)
            Events.remove(module)
            Commands.remove(module)
        }
    }

    static add(ModuleScript script) {
        moduleMap.put(moduleName, script)
    }
}
