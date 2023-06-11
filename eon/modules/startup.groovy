import net.dv8tion.jda.api.events.session.ReadyEvent
import sonnicon.eonbot.core.EventHandler
import sonnicon.eonbot.type.ExecutorFunc
import sonnicon.eonbot.type.MessageProxy
import sonnicon.eonbot.type.ModuleBase

class startup extends ModuleBase {

    void load(String context) {
        Closure readyEventClosure
        readyEventClosure = { ReadyEvent it ->
            println "Bot connected and ready!"
            EventHandler.remove(ReadyEvent.class, readyEventClosure)
        }
        EventHandler.register(ReadyEvent.class, readyEventClosure)
    }

    @ExecutorFunc("echo")
    boolean echo(Map<String, ?> data, MessageProxy message) {
        message.reply(data["text"] as String)
        true
    }
}