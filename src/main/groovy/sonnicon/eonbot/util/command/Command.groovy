package sonnicon.eonbot.util.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import sonnicon.eonbot.core.Modules

class Command {
    public final String name
    protected final CommandArg[] args
    protected final Closure closure

    protected requiredCount = 0, optionalCount = 0

    Command(String name, CommandArg[] args, Closure closure) {
        //verification
        boolean hadOptional = false
        for (int i = 0; i < args.length; i++) {
            if (!hadOptional) {
                if (args[i].required) {
                    requiredCount++
                } else {
                    hadOptional = true
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

        Commands.init()
        if (!Commands.commandMap.containsKey(Modules.moduleName))
            Commands.commandMap.put(Modules.moduleName, [:])
        Commands.commandMap.get(Modules.moduleName).put(name, this)
    }

    void call(MessageReceivedEvent event, List<String> inputArgs) {
        if (inputArgs.size() > requiredCount || inputArgs.size() < requiredCount + optionalCount) {
            //todo error message
            println "too many args"
            return
        }

        List<?> output = []
        for (int i = 0; i < inputArgs.size(); i++) {
            CommandArg arg = args[i]
            if (arg.possibilities != null && !(inputArgs[i] in arg.possibilities)) {
                //todo error message
                println "arg not in possibilities"
                return
            }
            output.add(args[i].getType().convert(inputArgs[i]))
        }

        closure(event, *output)
    }

    @Override
    String toString() {
        return name + " " + args.join(" ")
    }
}
