import net.dv8tion.jda.api.entities.Message
import sonnicon.eonbot.command.Commands
import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.type.ExecutorFunc
import sonnicon.eonbot.type.ModuleBase

//todo make message wrapper with all of this boilerplate
class modules extends ModuleBase {

    void load() {}

    void unload() {}

    @ExecutorFunc("loadModule")
    boolean loadModule(Map<String, ?> data, Message message) {
        String context = data.getOrDefault("context", Commands.getContext(message))
        boolean result = Modules.loadModule(data["module"] as String, context, data.get("force") as boolean)
        String response = "${result ? 'Loaded' : 'Failed to load'} module ${data["module"]} for context $context."
        message?.reply(response)?.queue()
        println(response)
        result
    }

    @ExecutorFunc("unloadModule")
    boolean unloadModule(Map<String, ?> data, Message message) {
        boolean result = Modules.unloadModule(data["module"] as String)
        String response = "${result ? 'Unloaded' : 'Failed to unload'} module ${data["module"]}."
        message?.reply(response)?.queue()
        println(response)
        result
    }

    @ExecutorFunc("loadClass")
    boolean loadClass(Map<String, ?> data, Message message) {
        boolean result = Modules.loadClass(data["module"] as String, data.get("force") as boolean)
        String response = "${result ? 'Loaded' : 'Failed to load'} class ${data["module"]}."
        message?.reply(response)?.queue()
        println(response)
        result
    }

    @ExecutorFunc("unloadClass")
    boolean unloadClass(Map<String, ?> data, Message message) {
        boolean result = Modules.unloadClass(data["module"] as String)
        String response = "${result ? 'Unloaded' : 'Failed to unload'} class ${data["module"]}."
        message?.reply(response)?.queue()
        println(response)
        result
    }

    @ExecutorFunc("loadInstance")
    boolean loadInstance(Map<String, ?> data, Message message) {
        String context = data.getOrDefault("context", Commands.getContext(message))
        boolean result = Modules.loadInstance(data["module"] as String, context, data.get("force") as boolean)
        String response = "${result ? 'Loaded' : 'Failed to load'} instance ${data["module"]} for context $context."
        message?.reply(response)?.queue()
        println(response)
        result
    }

    @ExecutorFunc("unloadInstance")
    boolean unloadInstance(Map<String, ?> data, Message message) {
        String context = data.getOrDefault("context", Commands.getContext(message))
        boolean result = Modules.unloadInstance(data["module"] as String, context)
        String response = "${result ? 'Unloaded' : 'Failed to unload'} module ${data["module"]} for context $context."
        message?.reply(response)?.queue()
        println(response)
        result
    }

    @ExecutorFunc("loaded")
    boolean loaded(Map<String, ?> data, Message message) {
        String response = "**ModuleMetas:**```\r\n" +
                Modules.moduleMetas.collect {it.key + ' = ' + it.value.lastChange}.join('\r\n') +
                "```\r\n**ModuleInstances:**\r\n```" +
                Modules.moduleInstances.collect {
                    "$it.key = [${it.value.keySet().join(', ')}]"
                }.join("\r\n") + "```"
        message?.reply(response)?.queue()
        println(response)
        true
    }
}