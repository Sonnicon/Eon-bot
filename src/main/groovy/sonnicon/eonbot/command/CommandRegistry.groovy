package sonnicon.eonbot.command

class CommandRegistry {
    // Map<context, Map<command, CmdNode>>
    static Map<String, Map<String, CmdNode>> commands = [:]
    // Map<context, Map<module, List<command>>>
    static Map<String, Map<String, List<String>>> moduleCommands = [:]

    static void register(String name, CmdNode node, String context = null, String module = null) {
        commands.putIfAbsent(context, [:])
        commands.get(context).put(name, node)
        if (module) {
            moduleCommands.putIfAbsent(context, [:])
            moduleCommands.get(context).putIfAbsent(module, [])
            moduleCommands.get(context).get(module).add(name)
        }
    }

    // probably shouldn't use this
    //todo safeties
    static void unregister(String name, String context = null) {
        Map<String, CmdNode> m1 = commands.get(context)
        m1?.remove(name)
        if (m1?.size() == 0) commands.remove(context)
        Map<String, List<String>> m2 = moduleCommands.get(context)
        m2.values().each { it.remove(name) }
    }

    //todo fix these
    static Map<String, Closure<Boolean>> executorMap
    static String name

    static void loadMap(Map<String, CmdNode> map, String context = null, String name = null, Map<String, Closure<Boolean>> executorMap) {
        this.executorMap = executorMap
        this.name = name
        map.each {
            commands.putIfAbsent(context, [:])
            commands.get(context).put(it.key, it.value as CmdNode)
            if (name) {
                moduleCommands.putIfAbsent(context, [:])
                moduleCommands.get(context).putIfAbsent(name, [])
                moduleCommands.get(context).get(name).add(it.key)
            }
        }
        this.executorMap = null
    }

    static void unloadModule(String name, String context = null) {
        Map<String, List<String>> map = moduleCommands.get(context)
        List<String> list = map?.get(name)
        if (list) {
            Map<String, CmdNode> comm = commands.get(context)
            list.each {
                comm.remove(it)
            }
            if (comm.size() == 0) {
                commands.remove(context)
            }
            map.remove(name)
            if (map.size() == 0) {
                moduleCommands.remove(context)
            }
        }
    }
}
