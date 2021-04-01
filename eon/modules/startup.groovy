import net.dv8tion.jda.api.events.ReadyEvent
import sonnicon.eonbot.core.EventHandler
import sonnicon.eonbot.core.Modules.ModuleBase
import sonnicon.eonbot.type.EventType
import sonnicon.eonbot.command.CommandRegistry

class ModuleStartup extends ModuleBase {

    void load() {
        Closure readyEventClosure
        readyEventClosure = { ReadyEvent it ->
            println "Bot connected and ready!"
            EventHandler.remove(EventType.onReady, readyEventClosure)
        }
        EventHandler.register(EventType.onReady, readyEventClosure)

        CommandRegistry.commands["echo"].executor = {data, message ->
            if(message) message.reply(data["text"]).queue()
            else println data["text"]
        }

        sonnicon.eonbot.core.Modules.load("modules")
    }

    void unload() {

    }
}