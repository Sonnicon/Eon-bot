import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.util.Files

import java.util.function.Function

static void main(arg) {
    HashMap<String, Integer> commandDefaults = [:]

    final HashMap<Integer, Function<MessageReceivedEvent, Boolean>> presets = [
            0: { m -> true },
            1: { m -> m.guild.getMember(m.author).roles.find { it.permissions.contains(Permission.ADMINISTRATOR) } },
            2: { m -> false }
    ]

    Commands.permissions = { s, e ->
        File f = new File(Files.permissions, s + ".yaml")
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

    Commands.Command.metaClass.defaultPermissions = { Integer i -> commandDefaults.put(name, i) }

    Commands commands = new Commands()

    commands.newCommand("permit", { event, args ->
        Long id = Long.parseLong(args.get(0).substring(3, args.get(0).length() - 1))
        String a = args.get(1)
        File f = new File(Files.permissions, a + ".yaml")
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

        event.channel.sendMessage("Set " + a + " override " + val + " for <@" + id + ">").queue()
    }).defaultPermissions(2)

    commands.newCommand("unpermit", { event, args ->
        Long id = Long.parseLong(args.get(0).substring(3, args.get(0).length() - 1))
        String a = args.get(1)
        File f = new File(Files.permissions, a + ".yaml")

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
                event.channel.sendMessage("Removed " + a + " overrides for <@" + id + ">").queue()
                return true
            }
        }
        event.channel.sendMessage("No " + a + " overrides exist for <@" + id + ">").queue()
    }).defaultPermissions(2)

    commands.newCommand("cmdpermit", { event, args ->
        String c = args.get(0)
        Integer i = Integer.parseInt(args.get(1))
        if (presets.containsKey(i)) {
            Commands.Command command = Commands.getCommand(c)
            if (command == null) {
                event.channel.sendMessage("Command " + c + " not found").queue()
            } else {
                command.defaultPermissions(i)
                event.channel.sendMessage("Set default permission for " + c + " to " + i).queue()
            }
        } else {
            event.channel.sendMessage("Permission preset " + i + " not found").queue()
        }
    }).defaultPermissions(2)
}

@Override
void unload() {
    Commands.permissions = { s, e -> true }
    Commands.Command.metaClass.defaultPermissions = { Integer i -> }
}