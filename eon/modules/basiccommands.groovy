import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import sonnicon.eonbot.util.Commands
import java.time.Instant

import static messagesutil

static void main(arg) {
    Commands commands = new Commands()

    commands.newCommand("echo", { event, args ->
        if (args.size() > 0) {
            messagesutil.reply(event, args.join(" "))
        }
    })

    commands.newCommand("list", { event, args ->
        def c = []
        if (args.size() == 0) {
            Commands.commandMap.values().each { it.values().each { c.add(it.name) } }
        } else {
            Commands.commandMap.get(args.get(0)).each { c.add(it.value.name) }
        }
        messagesutil.reply(event, "`" + c.join("` `") + "`")
    })

    commands.newCommand("ping", { event, args ->
        Instant created = event.message.getTimeCreated().toInstant()
        Instant now = Instant.now()
        Message m = event.channel.sendMessage("Handled in `" + now.compareTo(created) + "ms`").complete()
        m.editMessage(m.contentRaw + "\nResponded in `" + Instant.now().compareTo(now) + "ms`" +
                "\nTotal `" + Instant.now().compareTo(created) + "ms`").queue()
    })

    commands.newCommand("wipe", { event, args ->
        if (event.isFromType(ChannelType.TEXT)) {
            Integer i = Integer.parseUnsignedInt(args.get(0))
            event.channel.purgeMessages(event.channel.getHistory().retrievePast(i + 1).complete())
            messagesutil.reply(event, "Deleted " + i + " messages")
        } else {
            messagesutil.reply(event, "Cannot delete messages outside of server text channels")
        }
    }).defaultPermissions(1)
}