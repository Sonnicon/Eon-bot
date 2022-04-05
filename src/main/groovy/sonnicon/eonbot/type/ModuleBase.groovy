package sonnicon.eonbot.type

abstract class ModuleBase extends Script {

    void load() {}

    void unload() {}

    @Override
    final Object run() {
        this
    }
}
