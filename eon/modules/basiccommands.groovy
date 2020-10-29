import net.dv8tion.jda.api.entities.ChannelType
import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.util.Messages

static void main(arg) {
    Commands commands = new Commands()

    commands.newCommand("echo", { event, args ->
        if (args.size() > 0) {
            Messages.reply(event, args.join(" "))
        }
    })

    commands.newCommand("list", { event, args ->
        def c = []
        if (args.size() == 0) {
            Commands.commandMap.values().each { it.values().each { c.add(it.name) } }
        } else {
            Commands.commandMap.get(args.get(0)).each { c.add(it.value.name) }
        }
        Messages.reply(event, c.join(" "))
    })

    commands.newCommand("wipe", { event, args ->
        if (event.isFromType(ChannelType.TEXT)) {
            Integer i = Integer.parseUnsignedInt(args.get(0))
            event.channel.purgeMessages(event.channel.getHistory().retrievePast(i + 1).complete())
            Messages.reply(event, "Deleted " + i + " messages")
        } else {
            Messages.reply(event, "Cannot delete messages outside of server text channels")
        }
    }).defaultPermissions(1)
}