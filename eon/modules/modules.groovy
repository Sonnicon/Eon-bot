import sonnicon.eonbot.core.Modules

class ModuleStartup extends Modules.ModuleBase {

    void load() {}

    static Map<String, Closure> getExecutorMap() {
        ["load"    : { data, message ->
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
        }, "unload": { data, message ->
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
        }, "loaded": { data, message ->
            String response = Modules.loadedModules.keySet().toString()
            if (message) message.reply("`$response`").queue()
            else println(response)
            true
        }]
    }

    void unload() {}
}