package sonnicon.eonbot.type

/**
 * Base class for all modules.
 */
abstract class ModuleBase extends Script {

    /**
     * Called when module is loaded for a context.
     * @param context Context for which module is loaded
     */
    void load(String context) {}

    /**
     * Called when a module is unloaded for a context. Not called when bot is stopped.
     * @param context Context for which module is unloaded
     */
    void unload(String context) {}

    @Override
    final Object run() {
        this
    }
}
