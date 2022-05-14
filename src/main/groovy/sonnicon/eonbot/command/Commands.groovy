package sonnicon.eonbot.command


import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.bson.Document
import org.bson.types.ObjectId
import sonnicon.eonbot.core.Database
import sonnicon.eonbot.core.EventHandler
import sonnicon.eonbot.type.MessageProxy

class Commands {
    protected static final String PREFIX = '##'
    static final char[] quoteChars = ['"', '\'', '`', '“', '”', '’', '’'] as char[]

    protected static Closure messageListener = { MessageReceivedEvent event ->
        MessageProxy message = new MessageProxy(event.message)
        if (!event.author.isBot() && message.getContentRaw().startsWith(PREFIX)) {
            handleCommand(message.getContentRaw().substring(PREFIX.length()), message)
        }
    }

    static {
        EventHandler.register(MessageReceivedEvent.class, messageListener)
    }

    static void handleCommand(String string, MessageProxy message) {
        ArrayList<String> split = split(string)
        Map<String, ?> parsed = [:]
        String keyword = split.remove(0)

        CmdNode command = CommandRegistry.commands.get(message.getContext())?.get(keyword)
        if (!command) {
            if (message.getContext()) {
                command = CommandRegistry.commands.get(null)?.get(keyword)
            }
            if (!command) {
                message.reply("Command not found.")
                message.reactSuccess(false)
                return
            }
        }

        CmdResponse response = new CmdResponse()
        command.collect(response, split, parsed, message)
        boolean success = response.type == CmdResponse.CmdResponseType.success
        if (!success) {
            message.reply(response.type.name())
            message.reactSuccess(false)
        } else {
            message.reactSuccess(response.executor.call(parsed, message))
        }
    }

    static boolean checkPermissions(MessageProxy message, String commandid) {
        return checkPermissions(message.getAuthor().getIdLong(),
                message.isFromGuild() ? message.getGuild().getIdLong() : 0,
                message.isFromGuild() ? message.getMember().getRoles() + message.getGuild().getPublicRole() : null,
                commandid)
    }

    static boolean checkPermissions(long author, long guild, List<Role> roles, String commandid) {
        // Global
        Document x = Database.getUser(author, 0)
        if (x) {
            // User global
            if ((x.get("permissions") as Map).containsKey(commandid)) {
                return (x.get("permissions") as Map).get(commandid)
            }

            // Group
            if (x.containsKey("groups")) {
                for (ObjectId group : (x.get("groups") as List<ObjectId>)) {
                    x = Database.getGroupById(group)
                    if (x && (x.get("permissions") as Map).containsKey(commandid)) {
                        return (x.get("permissions") as Map).get(commandid)
                    }
                }
            }

            // Everyone group
            x = Database.getGroup("everyone")
            if (x.containsKey("permissions")) {
                Map permissions = x.get("permissions") as Map
                if (permissions.containsKey(commandid)) {
                    return permissions.get(commandid)
                }
            }
        }

        // User guild
        x = Database.getUser(author, guild)
        if (x && (x.get("permissions") as Map).containsKey(commandid)) {
            return (x.get("permissions") as Map).get(commandid)
        }

        // Role guild
        if (guild != 0) {
            for (Role role : roles) {
                x = Database.getRole(role.idLong)
                if (x && (x.get("permissions") as Map).containsKey(commandid)) {
                    return (x.get("permissions") as Map).get(commandid)
                }
            }
        }
        true
    }

    protected static List<String> split(String text) {
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
