import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.type.ExecutorFunc
import sonnicon.eonbot.type.MessageProxy
import sonnicon.eonbot.type.ModuleBase

//todo make message wrapper with all of this boilerplate
class modules extends ModuleBase {

    @ExecutorFunc("loadModule")
    boolean loadModule(Map<String, ?> data, MessageProxy message) {
        String context = data.getOrDefault("context", message.getContext())
        boolean result = Modules.loadModule(data["module"] as String, context, data.get("force") as boolean)
        message.reply("${result ? 'Loaded' : 'Failed to load'} module ${data["module"]} for context $context.")
        result
    }

    @ExecutorFunc("unloadModule")
    boolean unloadModule(Map<String, ?> data, MessageProxy message) {
        boolean result = Modules.unloadModule(data["module"] as String)
        message.reply("${result ? 'Unloaded' : 'Failed to unload'} module ${data["module"]}.")
        result
    }

    @ExecutorFunc("loadClass")
    boolean loadClass(Map<String, ?> data, MessageProxy message) {
        boolean result = Modules.loadClass(data["module"] as String, data.get("force") as boolean)
        message.reply("${result ? 'Loaded' : 'Failed to load'} class ${data["module"]}.")
        result
    }

    @ExecutorFunc("unloadClass")
    boolean unloadClass(Map<String, ?> data, MessageProxy message) {
        boolean result = Modules.unloadClass(data["module"] as String)
        message.reply("${result ? 'Unloaded' : 'Failed to unload'} class ${data["module"]}.")
        result
    }

    @ExecutorFunc("loadInstance")
    boolean loadInstance(Map<String, ?> data, MessageProxy message) {
        String context = data.getOrDefault("context", message.getContext())
        boolean result = Modules.loadInstance(data["module"] as String, context, data.get("force") as boolean)
        message.reply("${result ? 'Loaded' : 'Failed to load'} instance ${data["module"]} for context $context.")
        result
    }

    @ExecutorFunc("unloadInstance")
    boolean unloadInstance(Map<String, ?> data, MessageProxy message) {
        String context = data.getOrDefault("context", message.getContext())
        boolean result = Modules.unloadInstance(data["module"] as String, context)
        message.reply("${result ? 'Unloaded' : 'Failed to unload'} module ${data["module"]} for context $context.")
        result
    }

    @ExecutorFunc("loaded")
    boolean loaded(Map<String, ?> data, MessageProxy message) {
        message.reply("**ModuleMetas:**```\r\n" +
                Modules.moduleMetas.collect { it.key + ' = ' + it.value.lastChange }.join('\r\n') +
                "```\r\n**ModuleInstances:**\r\n```" +
                Modules.moduleInstances.collect {
                    "$it.key = [${it.value.keySet().join(', ')}]"
                }.join("\r\n") + "```")
        true
    }
}