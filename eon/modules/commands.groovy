import sonnicon.core.Events
import sonnicon.core.Modules
import sonnicon.type.EventType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

import java.util.function.BiConsumer

static void main(arg) {
    this["quoteChars"] = ["\"", "'", "`"] as char[]
    this["commandPrefix"] = "##"

    Events.on(EventType.MessageReceivedEvent, { MessageReceivedEvent event ->
        if (event.author.isBot() || !event.message.contentRaw.startsWith(this.commandPrefix)) return
        def input = splitArgs(event.message.contentRaw.substring(this.commandPrefix.length()))
        if (Command.commandMap.containsKey(input.get(0))) {
            Command.commandMap.get(input.get(0)).run(event, input.subList(1, input.size()))
        } else {
            event.channel.sendMessage("Command `" + input.get(0) + "` not found.").queue()
        }
    })
}

List<String> splitArgs(String input) {
    String[] splinput = input.split(" ")
    List<String> out = []
    StringJoiner joiner = new StringJoiner(" ")
    def currentQuote = -1

    for (str in splinput) {
        if (str.length() == 0) {
            out.add(str)
        } else if (currentQuote == -1) {
            if (str.charAt(0) in this.quoteChars) {
                char zero = str.charAt(0)
                if ((str.substring(str.length() - 1) as char) == zero) {
                    out.add(str.substring(1, str.length() - 1))
                } else {
                    currentQuote = this.quoteChars.findIndexOf { it == zero }
                    joiner.add(str.substring(1))
                }
            } else {
                out.add(str)
            }
        } else if (str.substring(str.length() - 1) as char == this.quoteChars[currentQuote] as char) {
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

class Command {
    final String name
    final BiConsumer<MessageReceivedEvent, List<String>> function

    static HashMap<String, Command> commandMap = [:]

    Command(String name, BiConsumer<MessageReceivedEvent, List<String>> function) {
        this.name = name
        this.function = function
        commandMap.put(name, this)
    }

    def run(MessageReceivedEvent event, List<String> args) {
        this.function.accept(event, args)
    }
}