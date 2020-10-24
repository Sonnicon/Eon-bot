package sonnicon.util

import sonnicon.core.Events
import sonnicon.core.Modules
import sonnicon.type.EventType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

import java.util.function.BiConsumer

class Commands {
    static final quoteChars = ["\"", "'", "`"] as char[]
    static final commandPrefix = "##"

    static  HashMap<String, HashMap<String, Command>> commandMap = [:]

    static init(){
        Events.on(EventType.MessageReceivedEvent, { MessageReceivedEvent event ->
            if (event.author.isBot() || !event.message.contentRaw.startsWith(commandPrefix)) return
            def input = splitArgs(event.message.contentRaw.substring(commandPrefix.length()))

            if(commandMap.find {
                if (it.value.containsKey(input.get(0))) {
                    it.value.get(input.get(0)).run(event, input.subList(1, input.size()))
                    return true
                }
                false
            } == null) {
                event.channel.sendMessage("Command `" + input.get(0) + "` not found.").queue()
            }
        })
    }

    static remove(String moduleName){
        commandMap.remove(moduleName)
    }

    static List<String> splitArgs(String input) {
        String[] splinput = input.split(" ")
        List<String> out = []
        StringJoiner joiner = new StringJoiner(" ")
        def currentQuote = -1

        for (str in splinput) {
            if (str.length() == 0) {
                out.add(str)
            } else if (currentQuote == -1) {
                if (str.charAt(0) in quoteChars) {
                    char zero = str.charAt(0)
                    if ((str.substring(str.length() - 1) as char) == zero) {
                        out.add(str.substring(1, str.length() - 1))
                    } else {
                        currentQuote = quoteChars.findIndexOf { it == zero }
                        joiner.add(str.substring(1))
                    }
                } else {
                    out.add(str)
                }
            } else if (str.substring(str.length() - 1) as char == quoteChars[currentQuote] as char) {
                if (str.length() == 1)
                    joiner.add("")
                else
                    joiner.add(str.substring(0, str.length() - 1))
                out.add(joiner.toString())
                joiner = new StringJoiner(" ")
                currentQuote = -1
            } else {
                joiner.add(str)
            }
        }
        if (joiner.length() > 0)
            out.add(joiner.toString())
        out
    }

    Command newCommand(String name, BiConsumer<MessageReceivedEvent, List<String>> function) {
        newCommand(name, function, Modules.moduleName)
    }

    Command newCommand(String name, BiConsumer<MessageReceivedEvent, List<String>> function, String moduleName) {
        new Command(name, function, moduleName)
    }

    class Command {
        final String name
        final BiConsumer<MessageReceivedEvent, List<String>> function

        Command(String name, BiConsumer<MessageReceivedEvent, List<String>> function, String moduleName) {
            this.name = name
            this.function = function
            if (!commandMap.containsKey(moduleName))
                commandMap.put(moduleName, [:])
            commandMap.get(moduleName).put(name, this)
        }

        def run(MessageReceivedEvent event, List<String> args) {
            this.function.accept(event, args)
        }
    }
}