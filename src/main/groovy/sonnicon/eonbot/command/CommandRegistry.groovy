package sonnicon.eonbot.command

/**
 * Utility class to register, unregister, and store, various commands and their handlers.
 */
class CommandRegistry {
    /**
     * Map between the context, and a map of command names to command nodes.
     * Used for looking up command nodes when a command is invoked.
     * Map<context, Map<command, CmdNode>>
     */
    static Map<String, Map<String, CmdNode>> commands = [:]
    /**
     * Map between the context, and a map of each module and list of it's command.
     * Used for easily removing all the commands of a specific module when unloaded.
     * Map<context, Map<module, List<command>>>
     */
    static Map<String, Map<String, List<String>>> moduleCommands = [:]


    /**
     * Registers a single command and it's node, for a given context and module. This will not link node executors.
     * @param name Name of command to be registered
     * @param node Node to be registered under the name
     * @param context Context for which command will be registered
     * @param module Module from which command originates
     */
    static void register(String name, CmdNode node, String context = null, String module = null) {
        commands.putIfAbsent(context, [:])
        commands.get(context).put(name, node)
        if (module) {
            moduleCommands.putIfAbsent(context, [:])
            moduleCommands.get(context).putIfAbsent(module, [])
            moduleCommands.get(context).get(module).add(name)
        }
    }

    /**
     * Unregisters a single command from a context.
     * "probably shouldn't use this"
     * @param name Name of command to unregister
     * @param context Context to unregister command from
     */
    // probably shouldn't use this
    //todo safeties
    static void unregister(String name, String context = null) {
        Map<String, CmdNode> m1 = commands.get(context)
        m1?.remove(name)
        if (m1?.size() == 0) commands.remove(context)
        Map<String, List<String>> m2 = moduleCommands.get(context)
        m2.values().each { it.remove(name)}
        Iterator iter = m2.entrySet().iterator()
        while (iter.hasNext()) {
            Map.Entry entry = iter.next()
            if (entry.getValue().isEmpty()) {
                iter.remove()
            }
        }
    }

    // Holders for temporary variables since we can't pass extra things into the slurper
    //todo fix these (//todo figure out what is broken)
    static Map<String, Closure<Boolean>> executorMap
    static String name

    /**
     * Loads a map of commands and their nodes, along with a context and module name, then associates them with a map of
     * handlers for each command.
     * @param map Map between command name and it's node
     * @param context Context for which commands will be added
     * @param name Name of module from which commands originate
     * @param executorMap Map between executor keys and their handlers
     */
    static void loadMap(Map<String, CmdNode> map, String context = null, String name = null, Map<String, Closure<Boolean>> executorMap) {
        this.executorMap = executorMap
        this.name = name
        map.each {
            // Add to list of commands for each context
            commands.putIfAbsent(context, [:])
            commands.get(context).put(it.key, it.value as CmdNode)
            // Add to list of commands for each context for each module if module given
            // We need this so we can remove commands later easily
            if (name) {
                moduleCommands.putIfAbsent(context, [:])
                moduleCommands.get(context).putIfAbsent(name, [])
                moduleCommands.get(context).get(name).add(it.key)
            }
        }
        this.executorMap = null
    }

    /**
     * Unregisters all the commands of a given module for a given context.
     * @param name Name of the module from which commands will be unregistered
     * @param context Context for which commands will be unregistered
     */
    static void unloadModule(String name, String context = null) {
        // Find all commands for this module for this context
        Map<String, List<String>> map = moduleCommands.get(context)
        List<String> list = map?.get(name)
        if (list) {
            // Get all commands in this context
            Map<String, CmdNode> comm = commands.get(context)
            // Remove all the commands from this module
            list.each {
                comm.remove(it)
            }
            // Prune empty entries
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
