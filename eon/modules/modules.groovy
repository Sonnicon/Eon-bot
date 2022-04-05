import net.dv8tion.jda.api.entities.Message
import sonnicon.eonbot.command.Commands
import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.type.ExecutorFunc
import sonnicon.eonbot.type.ModuleBase

//todo this needs big updating
class modules extends ModuleBase {

    void load() {}

    @ExecutorFunc("load")
    boolean loadModule(Map<String, ?> data, Message message) {
        String response
        if (Modules.loadModule(data["name"] as String, Commands.getContext(message), true)) {
            response = "Loaded module"
            if (message) message.reply(response).queue()
            else println(response)
            return true
        } else {
            response = "Failed to load module"
            if (message) message.reply(response).queue()
            else println(response)
            return false
        }
    }

    @ExecutorFunc("unload")
    boolean unloadModule(Map<String, ?> data, Message message) {
        String response
        if (Modules.unloadModule(data["name"] as String, Commands.getContext(message))) {
            response = "Unloaded module"
            if (message) message.reply(response).queue()
            else println(response)
            return true
        } else {
            response = "Failed to unload module"
            if (message) message.reply(response).queue()
            else println(response)
            return false
        }
    }

    @ExecutorFunc("loaded")
    boolean loadedModules(Map<String, ?> data, Message message) {
        String response = (Modules.moduleInstances.getOrDefault(Commands.getContext(message), [:])?.keySet() +
                Modules.moduleInstances.getOrDefault(null, [:]).keySet()).toString()
        if (message) message.reply("`$response`").queue()
        else println(response)
        true
    }

    void unload() {}
}