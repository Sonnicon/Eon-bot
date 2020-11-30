import messagesutil
import net.dv8tion.jda.api.entities.ChannelType
import sonnicon.eonbot.util.command.Command
import sonnicon.eonbot.util.command.CommandArg
import sonnicon.eonbot.util.command.CommandArgType
import sonnicon.eonbot.util.command.Commands

static void main(args) {
    new Command("echo", [new CommandArg(CommandArgType.getType("String"), "Input Text")] as CommandArg[],
            { event, arg ->
                messagesutil.reply(event, arg)
            }, true)

    new Command("list", [new CommandArg(CommandArgType.getType("String"), "Module", false)] as CommandArg[],
            { event, arg = "" ->
                def c = []
                if (arg.size() == 0) {
                    Commands.commandMap.values().each { it.values().each { c.add(it.name) } }
                } else {
                    Commands.commandMap.get(arg.get(0)).each { c.add(it.value.name) }
                }
                messagesutil.reply(event, "`" + c.join("` `") + "`")
            })

    new Command("wipe", [new CommandArg(CommandArgType.getType("Integer"), "Amount")] as CommandArg[],
            { event, arg ->
                if (event.isFromType(ChannelType.TEXT)) {
                    event.channel.purgeMessages(event.channel.getHistory().retrievePast(arg + 1).complete())
                    messagesutil.reply(event, "Deleted " + arg + " messages")
                } else {
                    messagesutil.reply(event, "Cannot delete messages outside of server text channels")
                }
            }).defaultPermissions(1)

    new Command("github", [] as CommandArg[],
            { event ->
                messagesutil.reply(event, "https://github.com/Sonnicon/Eon-bot")
            })
}