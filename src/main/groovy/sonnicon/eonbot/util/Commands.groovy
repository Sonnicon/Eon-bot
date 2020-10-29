package sonnicon.eonbot.util

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import sonnicon.eonbot.Eonbot
import sonnicon.eonbot.core.Events
import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.type.EventType

import java.util.function.BiConsumer
import java.util.function.BiFunction

class Commands {
    static final char[] quoteChars = ["\"", "'", "`"] as char[]
    static final String commandPrefix = "##"

    static HashMap<String, HashMap<String, Command>> commandMap = [:]
    static BiFunction<String, MessageReceivedEvent, Boolean> permissions = { s, e -> true }

    static init() {
        Events.on(EventType.MessageReceivedEvent, { MessageReceivedEvent event ->
            if (event.author.isBot() || !event.message.contentRaw.startsWith(commandPrefix)) return
            def input = splitArgs(event.message.contentRaw.substring(commandPrefix.length()))

            Command c = getCommand(input.get(0))
            if (c == null) {
                Messages.reply(event, "Command `" + input.get(0) + "` not found.")
            } else {
                c.run(event, input.subList(1, input.size()))
            }
        })
    }

    static Command getCommand(String command) {
        for (HashMap<String, Command> map : commandMap.values()) {
            if (map.containsKey(command)) {
                return map.get(command)
            }
        }
        return null
    }

    static remove(String moduleName) {
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
            if (Eonbot.config.operators.contains(event.author.idLong) || permissions.apply(name, event)) {
                try {
                    this.function.accept(event, args)
                }catch(Exception ex){
                    ex.printStackTrace()
                    Messages.reply(event, "An error has occurred running this command.")
                }
            } else {
                Messages.reply(event, "You do not have permission to run this command.")
            }
        }
    }
}