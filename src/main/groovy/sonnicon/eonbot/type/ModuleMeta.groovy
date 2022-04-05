package sonnicon.eonbot.type

import sonnicon.eonbot.command.CmdNode

class ModuleMeta {
    Map<String, CmdNode> commands = null
    List<String> dependencies = null
    boolean isShared = true

    transient long lastChange = -1
    transient Class<ModuleBase> moduleBase = null
}
