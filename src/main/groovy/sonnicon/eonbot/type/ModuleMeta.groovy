package sonnicon.eonbot.type

import sonnicon.eonbot.command.CmdNode

/**
 * Metadata about a module.
 */
class ModuleMeta {
    /**
     * Map of root command nodes associated with this module.
     */
    Map<String, CmdNode> commands = null
    /**
     * List of names of modules this module depends on.
     */
    List<String> dependencies = null
    /**
     * Whether the module has separate instances for each context.
     */
    boolean isShared = true

    /**
     * When the latest change to the source files was when this meta was loaded.
     */
    transient long lastChange = -1
    /**
     * Class of the module this meta refers to.
     */
    transient Class<ModuleBase> moduleBase = null
}
