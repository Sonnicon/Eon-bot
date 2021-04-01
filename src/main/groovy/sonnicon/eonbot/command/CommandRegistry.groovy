package sonnicon.eonbot.command

import sonnicon.eonbot.core.FileIO

class CommandRegistry {
    static Map<String, CmdNode> commands = [:]
    static Map<String, List<String>> moduleCommands = [:]

    static void register(String name, CmdNode node, String module = null){
        commands.put(name, node)
        if(module) moduleCommands.put(module, moduleCommands.getOrDefault(module, []) + name)
    }

    static void unregister(String name){
        commands.remove(name)
        moduleCommands.values().each {it.remove(name)}
    }

    static void loadYaml(File file, String name = null) {
        (FileIO.yamlSlurper.parse(file) as Map<String, CmdNode>).each {
            commands.put(it.key, it.value as CmdNode)
            // beautiful
            if(name) moduleCommands.put(name, moduleCommands.getOrDefault(name, []) + it.key)
        }
    }

    static void unloadModule(String name){
        if(!moduleCommands.containsKey(name)) return
        moduleCommands.get(name).each {commands.remove(it)}
        moduleCommands.remove(name)
    }
}
