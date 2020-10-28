import sonnicon.eonbot.util.Commands
import net.dv8tion.jda.internal.entities.ReceivedMessage

static void main(arg) {
    Commands commands = new Commands()

    commands.newCommand("echo", { event, args ->
        if (args.size() > 0)
            event.channel.sendMessage(args.join(" ")).queue()
    })

    commands.newCommand("list", { event, args ->
        def c = []
        if (args.size() == 0) {
            Commands.commandMap.values().each { it.values().each { c.add(it.name) } }
        } else {
            Commands.commandMap.get(args.get(0)).each { c.add(it.value.name) }
        }
        event.channel.sendMessage(c.join(" ")).queue()
    })

    commands.newCommand("wipe", { event, args ->
        if(args.size() == 1){
            Integer i = Integer.parseUnsignedInt(args.get(0))
            event.channel.purgeMessages(event.channel.getHistory().retrievePast(i + 1).complete())
            event.channel.sendMessage("Deleted " + i + " messages").queue()
        }
    })
}