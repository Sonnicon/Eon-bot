import net.dv8tion.jda.api.entities.Message
import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.type.ExecutorFunc

class ModuleStartup extends Modules.ModuleBase {

    void load() {}

    @ExecutorFunc("load")
    boolean loadModule(Map<String, ?> data, Message message) {
        String response
        if (Modules.load(data["name"], true)) {
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
        if (Modules.unload(data["name"])) {
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
        String response = Modules.loadedModules.keySet().toString()
        if (message) message.reply("`$response`").queue()
        else println(response)
        true
    }

    void unload() {}
}