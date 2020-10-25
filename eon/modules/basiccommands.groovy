import sonnicon.eonbot.util.Commands

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
}