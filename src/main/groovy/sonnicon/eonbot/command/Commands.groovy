package sonnicon.eonbot.command

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import sonnicon.eonbot.core.EventHandler
import sonnicon.eonbot.type.EventType

class Commands {
    protected static final String prefix = '##'
    static final char[] quoteChars = ['"', '\'', '`', '“', '”', '’', '’'] as char[]

    protected static Closure messageListener = { MessageReceivedEvent event ->
        if (!event.author.isBot() && event.message.getContentRaw().startsWith(prefix)) {
            handleCommand(event.message.getContentRaw().substring(prefix.length()), event.message as Message)
        }
    }

    static {
        EventHandler.register(EventType.onMessageReceived, messageListener)
    }

    static void handleCommand(String string, Message message = null) {
        ArrayList<String> split = split(string)
        Map<String, ?> parsed = [:]

        CmdNode command = CommandRegistry.commands.get(split.remove(0))
        if (!command) {
            if (message) message.reply("Command not found").queue()
            else println("Command not found")
            return
        }

        CmdResponse response = new CmdResponse()
        command.collect(response, split, parsed)
        if (response.type == CmdResponse.CmdResponseType.success) {
            response.executor.call(parsed, message)
        } else if (message) {
            message.reply(response.type.name()).queue()
        } else {
            println(response.type.name())
        }
    }

    static List<String> split(String text) {
        String[] splinput = text.split(" ")
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
