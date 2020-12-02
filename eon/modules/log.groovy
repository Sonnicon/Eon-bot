import sonnicon.eonbot.core.Events
import sonnicon.eonbot.type.EventType
import sonnicon.eonbot.util.Files

static void main(arg) {
    File log = new File(Files.main, "log.txt")
    BufferedWriter writer = new BufferedWriter(new FileWriter(log, true))

    Events.on(EventType.MessageReceivedEvent, { event ->
        if (!event.author.isBot() && event.message.contentRaw.startsWith(sonnicon.eonbot.util.command.Commands.commandPrefix)) {
            writer.append("[" + new Date() + "] " + "<" + event.author.id + "> " + event.message.contentRaw + "\n")
            writer.flush()
        }
    })

    writer.append("[" + new Date() + "] Loaded log module\n")
    writer.flush()
}