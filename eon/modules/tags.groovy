import messagesutil
import sonnicon.eonbot.util.Files
import sonnicon.eonbot.util.command.Command
import sonnicon.eonbot.util.command.CommandArg
import sonnicon.eonbot.util.command.CommandArgType

static void main(arg) {
    final File file = new File(Files.modules, "tags/")
    file.mkdirs()

    HashMap<Long, HashMap<String, String>> tags = [:]

    Closure<Boolean> load = { Long id ->
        if (tags.containsKey(id))
            return false
        target = getTarget(id)
        if (target.exists()) {
            FileReader reader = new FileReader(target)
            tags.put(id, Files.yaml.load(reader))
            reader.close()
        } else {
            tags.put(id, [:])
        }
        return true
    }

    Closure save = { Long id ->
        File target = getTarget(id)
        FileWriter writer = new FileWriter(target)
        Files.yaml.dump(tags.get(id), writer)
        writer.close()
    }

    new Command("tag add", [new CommandArg(CommandArgType.getType("String"), "Name"), new CommandArg(CommandArgType.getType("String"), "Result")] as CommandArg[], { event, arg1, arg2 ->
        load.call(event.guild.idLong)
        tags.get(event.guild.idLong).put(arg1, arg2)
        save.call(event.guild.idLong)
        messagesutil.reply(event, "Created tag `" + arg1 + "`")
    }, true)

    new Command("tag remove", [new CommandArg(CommandArgType.getType("String"), "Name")] as CommandArg[], { event, arg1 ->
        load.call(event.guild.idLong)
        tags.get(event.guild.idLong).remove(arg1)
        save.call(event.guild.idLong)
        messagesutil.reply(event, "Created tag `" + arg1 + "`")
    })

    new Command("tags", [] as CommandArg[], { event ->
        load.call(event.guild.idLong)
        if (tags.get(event.guild.idLong).size() > 0) {
            messagesutil.reply(event, "Found tags: " + tags.get(event.guild.idLong).keySet().collect { "`$it`" })
        } else {
            messagesutil.reply(event, "No tags found.")
        }
    })

    new Command("tag", [new CommandArg(CommandArgType.getType("String"), "Name")] as CommandArg[], { event, arg1 ->
        load.call(event.guild.idLong)
        if (tags.get(event.guild.idLong).containsKey(arg1)) {
            messagesutil.reply(event, tags.get(event.guild.idLong).get(arg1))
        } else {
            messagesutil.reply(event, "Tag not found.")
        }
    }, true)
}

static File getTarget(long id) {
    new File(Files.modules, "tags/" + id + ".yaml")
}