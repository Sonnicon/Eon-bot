import sonnicon.eonbot.util.Commands

static void main(arg){
    commands = new Commands()

    commands.newCommand("echo",  { event, args ->
        if (args.size() > 0)
            event.channel.sendMessage(args.join(" ")).queue()
    })
}