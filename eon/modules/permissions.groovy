import messagesutil
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import sonnicon.eonbot.util.Files
import sonnicon.eonbot.util.command.Command
import sonnicon.eonbot.util.command.CommandArg
import sonnicon.eonbot.util.command.CommandArgType
import sonnicon.eonbot.util.command.Commands

import java.util.function.Function

static void main(arg) {
    final File permissions = new File(Files.main, "perms/")
    permissions.mkdirs()

    HashMap<String, Integer> commandDefaults = [:]

    final HashMap<Integer, Function<MessageReceivedEvent, Boolean>> presets = [
            0: { m -> true },
            1: { m ->
                if (m.isFromType(ChannelType.TEXT)) {
                    return m.getMember().hasPermission(Permission.ADMINISTRATOR)
                } else {
                    return true
                }
            },
            2: { m -> false }
    ]

    Commands.metaClass.static.permissions = { String s, MessageReceivedEvent e ->
        File f = new File(permissions, s + ".yaml")
        if (f.exists()) {
            FileReader reader = new FileReader(f)
            HashMap<Long, Boolean> map = Files.yaml.load(reader)
            reader.close()
            if (map.containsKey(e.author.idLong)) {
                return map.get(e.author.idLong)
            }
        }
        if (commandDefaults.containsKey(s)) {
            return presets.get(commandDefaults.get(s)).doCall(e)
        }
        true
    }

    Command.metaClass.defaultPermissions = { Integer i -> commandDefaults.put(name, i) }

    new Command("permit", [new CommandArg(CommandArgType.getType("User"), "Target"),
                           new CommandArg(CommandArgType.getType("Command"), "Command"),
                           new CommandArg(CommandArgType.getType("Boolean"), "Setting")]
            as CommandArg[], {
        event, arg1, arg2, arg3 ->
            if (!Files.verify(arg2.name)) {
                messagesutil.reply(event, "Illegal value")
                return
            }
            File f = new File(permissions, arg2.name + ".yaml")
            HashMap<Long, Boolean> map
            if (f.exists()) {
                FileReader reader = new FileReader(f)
                map = Files.yaml.load(reader)
                reader.close()
            } else {
                map = new HashMap<>()
                f.createNewFile()
            }
            map.put(arg1, arg3)

            FileWriter writer = new FileWriter(f)
            Files.yaml.dump(map, writer)
            writer.close()

            messagesutil.reply(event, "Set " + arg2.name + " override " + arg3 + " for <@" + arg1 + ">", false)
    }).defaultPermissions(2)

    new Command("unpermit", [new CommandArg(CommandArgType.getType("User"), "Target"),
                             new CommandArg(CommandArgType.getType("Command"), "Command")]
            as CommandArg[], {
        event, arg1, arg2 ->
            if (!Files.verify(arg2.name)) {
                messagesutil.reply(event, "Illegal value")
                return
            }
            File f = new File(permissions, arg2.name + ".yaml")

            HashMap<Long, Boolean> map
            if (f.exists()) {
                FileReader reader = new FileReader(f)
                map = Files.yaml.load(reader)
                reader.close()

                if (map.containsKey(arg1)) {
                    map.remove(arg1)
                    FileWriter writer = new FileWriter(f)
                    Files.yaml.dump(map, writer)
                    writer.close()
                    messagesutil.reply(event, "Removed " + arg2.name + " overrides for <@" + arg1 + ">", false)
                    return true
                }
            }
            messagesutil.reply(event, "No " + arg2.name + " overrides exist for <@" + arg1 + ">", false)
    }).defaultPermissions(2)

    new Command("cmdpermit", [new CommandArg(CommandArgType.getType("Command"), "Command"),
                              new CommandArg(CommandArgType.getType("Integer"), "Level")]
            as CommandArg[], {
        event, arg1, arg2 ->
            if (presets.containsKey(arg2)) {
                if (command == null) {
                    messagesutil.reply(event, "Command " + arg1.name + " not found")
                } else {
                    arg1.defaultPermissions(arg2)
                    messagesutil.reply(event, "Set default permission for " + arg1.name + " to " + arg2)
                }
            } else {
                messagesutil.reply(event, "Permission preset " + arg2 + " not found")
            }
    }).defaultPermissions(2)
}

@Override
void unload() {
    Commands.permissions = { s, e -> true }
    Command.metaClass.defaultPermissions = { Integer i -> }
}