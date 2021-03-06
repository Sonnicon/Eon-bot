package sonnicon.eonbot.util.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import sonnicon.eonbot.Eonbot
import sonnicon.eonbot.core.Events
import sonnicon.eonbot.type.EventType

import java.util.function.BiFunction

class Commands {
    static final char[] quoteChars = ["\"", "'", "`", "“", "”", "’", "’"] as char[]
    static final String commandPrefix = "##"

    static HashMap<String, HashMap<String, Command>> commandMap = [:]

    protected static boolean inited = false

    static init() {
        if(inited) return
        inited = true

        Events.on(EventType.MessageReceivedEvent, { MessageReceivedEvent event ->
            if (event.author.isBot() || !event.message.contentRaw.startsWith(commandPrefix)) return
            def input = splitArgs(event.message.contentRaw.substring(commandPrefix.length()))

            Command c = getCommand(input.get(0))
            if (c == null) {
                event.channel.sendMessage("Command `" + input.get(0).replaceAll("[@]", "") + "` not found.").queue()
            } else if (Eonbot.config.operators.contains(event.author.idLong) || permission(c.name, event)) {
                c.call(event, input.subList(1, input.size()))
            } else {
                errorNoPermission(event, c)
            }
        })
    }

    static boolean permission(String command, MessageReceivedEvent event){
        true
    }

    static void errorNoPermission(MessageReceivedEvent event, Command command){
        event.channel.sendMessage(event.author.getAsMention() + " You do not have permission to run this command.")
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