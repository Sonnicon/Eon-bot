package sonnicon.eonbot.command

class CommandRegistry {
    static Map<String, CmdNode> commands = [:]
    static Map<String, List<String>> moduleCommands = [:]

    static void register(String name, CmdNode node, String module = null) {
        commands.put(name, node)
        if (module) moduleCommands.put(module, moduleCommands.getOrDefault(module, []) + name)
    }

    static void unregister(String name) {
        commands.remove(name)
        moduleCommands.values().each { it.remove(name) }
    }

    static Map<String, Closure> executorMap

    static void loadMap(Map<String, CmdNode> map, String name = null, Map<String, Closure> executorMap = null) {
        this.executorMap = executorMap
        map.each {
            commands.put(it.key, it.value as CmdNode)
            // beautiful
            if (name) moduleCommands.put(name, moduleCommands.getOrDefault(name, []) + it.key)
        }
        this.executorMap = null
    }

    static void unloadModule(String name) {
        if (!moduleCommands.containsKey(name)) return
        moduleCommands.get(name).each { commands.remove(it) }
        moduleCommands.remove(name)
    }
}
