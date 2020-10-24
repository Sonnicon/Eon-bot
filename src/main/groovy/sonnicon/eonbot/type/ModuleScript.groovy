package sonnicon.eonbot.type

import sonnicon.eonbot.core.Modules

class ModuleScript extends Script {
    ModuleScript() {
        Modules.add(this)
    }

    void unload() {}

    @Override
    Object run() {
        null
    }
}
