package sonnicon.eonbot.util.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import sonnicon.eonbot.Eonbot
import sonnicon.eonbot.core.Events
import sonnicon.eonbot.type.EventType

import java.util.function.BiFunction

class Commands {
    static final char[] quoteChars = ["\"", "'", "`"] as char[]
    static final String commandPrefix = "##"

    static HashMap<String, HashMap<String, Command>> commandMap = [:]
    static BiFunction<String, MessageReceivedEvent, Boolean> permissions = { s, e -> true }

    static init() {
        new CommandArgType<String>("String")
        new CommandArgType<Integer>("numbr")

        new Command("testcommand", [new CommandArg(CommandArgType.getType("String"), "abcd", ["one", "two", "three"] as String[]), new CommandArg(CommandArgType.getType("String"), "string", false)] as CommandArg[], {
            testArg, string = null ->
                println testArg
        }).call(null, ["two"])

        println getCommand("testcommand").toString()


        Events.on(EventType.MessageReceivedEvent, { MessageReceivedEvent event ->
            if (event.author.isBot() || !event.message.contentRaw.startsWith(commandPrefix)) return
            def input = splitArgs(event.message.contentRaw.substring(commandPrefix.length()))

            Command c = getCommand(input.get(0))
            if (c == null) {
                event.channel.sendMessage("Command `" + input.get(0).replaceAll("[@]", "") + "` not found.").queue()
            } else if (Eonbot.config.operators.contains(event.author.idLong) || permissions.apply(name, event)) {
                c.call(event, input.subList(1, input.size()))
            } else {
                //todo error message
                println "no permission"
            }
        })
    }

    static Command getCommand(String command) {
        for (HashMap<String, Command> map : commandMap.values()) {
            if (map.containsKey(command)) {
                return map.get(command)
            }
        }
        null
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
}