package sonnicon.type

import sonnicon.core.Modules

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
