import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.ReadyEvent
import sonnicon.eonbot.core.EventHandler
import sonnicon.eonbot.core.Modules.ModuleBase
import sonnicon.eonbot.type.EventType
import sonnicon.eonbot.type.ExecutorFunc

class ModuleStartup extends ModuleBase {

    void load() {
        Closure readyEventClosure
        readyEventClosure = { ReadyEvent it ->
            println "Bot connected and ready!"
            EventHandler.remove(EventType.onReady, readyEventClosure)
        }
        EventHandler.register(EventType.onReady, readyEventClosure)
    }

    @ExecutorFunc("echo")
    boolean echo(data, message) {
        if (message) message.reply(data["text"]).queue()
        else println data["text"]
        true
    }

    void unload() {

    }
}