import sonnicon.eonbot.command.CommandRegistry
import sonnicon.eonbot.core.Modules

class ModuleStartup extends Modules.ModuleBase {

    void load() {
        CommandRegistry.commands["load"].executor = { data, message ->
            String response
            if (Modules.load(data["name"])) {
                response = "Loaded module"
                if (message) message.reply(response).queue()
                else println(response)
            } else {
                response = "Failed to load module"
                if (message) message.reply(response).queue()
                else println(response)
            }
        }

        CommandRegistry.commands["unload"].executor = { data, message ->
            String response
            if (Modules.unload(data["name"])) {
                response = "Unloaded module"
                if (message) message.reply(response).queue()
                else println(response)
            } else {
                response = "Failed to unload module"
                if (message) message.reply(response).queue()
                else println(response)
            }
        }

        CommandRegistry.commands["loaded"].executor = { data, message ->
            String response = Modules.loadedModules.keySet().toString()
            if (message) message.reply("`$response`").queue()
            else println(response)

        }
    }

    void unload() {

    }
}