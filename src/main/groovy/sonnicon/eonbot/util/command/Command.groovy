package sonnicon.eonbot.util.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import sonnicon.eonbot.core.Modules

class Command {
    public final String name
    protected final CommandArg[] args
    protected final Closure closure
    protected final boolean extend

    protected requiredCount = 0, optionalCount = 0

    Command(String name, CommandArg[] args, Closure closure, boolean extend = false) {
        //verification
        if(extend && args.length == 0){
            throw new IllegalArgumentException("[" + Modules.moduleName + "] Cannot have extend with no arguments for command " + name)
        }
        boolean hadOptional = false
        for (int i = 0; i < args.length; i++) {
            if (!hadOptional) {
                if (args[i].required) {
                    requiredCount++
                } else {
                    hadOptional = true
                    optionalCount++
                }
            } else {
                optionalCount++
                if (hadOptional && args[i].required) {
                    throw new IllegalArgumentException("[" + Modules.moduleName + "] Cannot have required arguments after optional arguments for command " + name)
                }
            }
        }

        this.name = name
        this.args = args
        this.closure = closure
        this.extend = extend

        Commands.init()
        if (!Commands.commandMap.containsKey(Modules.moduleName))
            Commands.commandMap.put(Modules.moduleName, [:])
        Commands.commandMap.get(Modules.moduleName).put(name, this)
    }

    void call(MessageReceivedEvent event, List<String> inputArgs) {
        if (!extend && inputArgs.size() > requiredCount + optionalCount) {
            errorExcessArgs(event, this)
            return
        }else if (inputArgs.size() < requiredCount){
            errorMissingArgs(event, this)
            return
        }

        List<?> output = []
        for (int i = 0; i < inputArgs.size() && i < args.size(); i++) {
            CommandArg arg = args[i]
            String str
            if(extend && i + 1 == args.size()){
                str = String.join(" ", inputArgs.subList(i, inputArgs.size()))
            }else{
                str = inputArgs[i]
            }
            if (arg.possibilities != null && !(str in arg.possibilities)) {
                errorUnknownArgs(event, this)
                return
            }
            output.add(args[i].getType().convert(str))
        }

        closure(event, *output)
    }

    static errorMissingArgs(MessageReceivedEvent event, Command command){
        event.channel.sendMessage(event.author.getAsMention() + " Missing command arguments.\n Correct usage: " + command.toString())
    }

    static errorExcessArgs(MessageReceivedEvent event, Command command){
        event.channel.sendMessage(event.author.getAsMention() + " Too many command arguments.\n Correct usage: " + command.toString())
    }

    static errorUnknownArgs(MessageReceivedEvent event, Command command){
        event.channel.sendMessage(event.author.getAsMention() + " Argument not in options.\n Correct usage: " + command.toString())
    }

    @Override
    String toString() {
        return name + " " + args.join(" ") + (extend ? "..." : "")
    }
}
