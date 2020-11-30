import messagesutil
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import sonnicon.eonbot.util.Files
import sonnicon.eonbot.util.command.Command
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

    Commands.permissions = { s, e ->
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


    /*commands.newCommand("permit", { event, args ->
        Long id = Long.parseLong(args.get(0).substring(3, args.get(0).length() - 1))
        String a = args.get(1)
        if (!Files.verify(a)) {
            messagesutil.reply(event, "Illegal value")
            return
        }
        File f = new File(permissions, a + ".yaml")
        HashMap<Long, Boolean> map
        if (f.exists()) {
            FileReader reader = new FileReader(f)
            map = Files.yaml.load(reader)
            reader.close()
        } else {
            map = new HashMap<>()
            f.createNewFile()
        }
        Boolean val = Boolean.parseBoolean(args.get(2))
        map.put(id, val)

        FileWriter writer = new FileWriter(f)
        Files.yaml.dump(map, writer)
        writer.close()

        messagesutil.reply(event, "Set " + a + " override " + val + " for <@" + id + ">")
    }).defaultPermissions(2)

    commands.newCommand("unpermit", { event, args ->
        Long id = Long.parseLong(args.get(0).substring(3, args.get(0).length() - 1))
        String a = args.get(1)
        if (!Files.verify(a)) {
            messagesutil.reply(event, "Illegal value")
            return
        }
        File f = new File(permissions, a + ".yaml")

        HashMap<Long, Boolean> map
        if (f.exists()) {
            FileReader reader = new FileReader(f)
            map = Files.yaml.load(reader)
            reader.close()

            if (map.containsKey(id)) {
                map.remove(id)
                FileWriter writer = new FileWriter(f)
                Files.yaml.dump(map, writer)
                writer.close()
                messagesutil.reply(event, "Removed " + a + " overrides for <@" + id + ">", false)
                return true
            }
        }
        messagesutil.reply(event, "No " + a + " overrides exist for <@" + id + ">", false)
    }).defaultPermissions(2)

    commands.newCommand("cmdpermit", { event, args ->
        String c = args.get(0)
        Integer i = Integer.parseInt(args.get(1))
        if (presets.containsKey(i)) {
            Command command = Commands.getCommand(c)
            if (command == null) {
                messagesutil.reply(event, "Command " + c + " not found")
            } else {
                command.defaultPermissions(i)
                messagesutil.reply(event, "Set default permission for " + c + " to " + i)
            }
        } else {
            messagesutil.reply(event, "Permission preset " + i + " not found")
        }
    }).defaultPermissions(2)*/
}

@Override
void unload() {
    Commands.permissions = { s, e -> true }
    Command.metaClass.defaultPermissions = { Integer i -> }
}