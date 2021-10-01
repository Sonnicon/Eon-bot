import net.dv8tion.jda.api.events.ReadyEvent
import sonnicon.eonbot.core.EventHandler
import sonnicon.eonbot.core.Modules.ModuleBase
import sonnicon.eonbot.type.EventType

class ModuleStartup extends ModuleBase {

    void load() {
        Closure readyEventClosure
        readyEventClosure = { ReadyEvent it ->
            println "Bot connected and ready!"
            EventHandler.remove(EventType.onReady, readyEventClosure)
        }
        EventHandler.register(EventType.onReady, readyEventClosure)
    }

    static Map<String, Closure> getExecutorMap() {
        ["echo": { data, message ->
            if (message) message.reply(data["text"]).queue()
            else println data["text"]
            true
        }]
    }

    void unload() {

    }
}