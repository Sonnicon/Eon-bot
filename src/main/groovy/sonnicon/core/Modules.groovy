package sonnicon.core

import org.codehaus.groovy.control.CompilerConfiguration
import sonnicon.Eonbot
import sonnicon.type.ModuleScript
import sonnicon.util.Files

class Modules {
    static moduleMap = [:]

    private static GroovyScriptEngine gse
    private static moduleName = ""

    static init() {
        CompilerConfiguration config = new CompilerConfiguration()
        config.recompileGroovySource = false
        config.setScriptBaseClass(ModuleScript.name.toString())

        gse = new GroovyScriptEngine(Files.modules.path, new GroovyClassLoader(Eonbot.getClassLoader(), config))
        gse.setConfig(config)
    }

    static loadModule(String module) {
        loadModule(module, "")
    }

    static loadModule(String module, String args) {
        moduleName = module
        Binding b = new Binding([arg: args, "moduleName": module])
        gse.run(module + ".groovy", b)
    }

    static unloadModule(String module) {
        if (moduleMap.containsKey(module)) {
            (moduleMap.get(module) as ModuleScript).unload()
            moduleMap.remove(module)
            Events.remove(module)
        }
    }

    static add(ModuleScript script) {
        moduleMap.put(moduleName, script)
    }
}
