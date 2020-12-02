import messagesutil
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent
import sonnicon.eonbot.core.Events
import sonnicon.eonbot.type.EventType
import sonnicon.eonbot.util.Files
import sonnicon.eonbot.util.command.Command
import sonnicon.eonbot.util.command.CommandArg

import java.util.function.Function

static void main(arg) {
    HashMap<Long, Long> channels = [:]
    if (getFile().exists()) {
        FileReader reader = new FileReader(getFile())
        channels = Files.yaml.load(reader)
        reader.close()
    }

    new Command("setLogChannel", [] as CommandArg[], { event ->
        channels.put(event.guild.idLong, event.channel.idLong)
        messagesutil.reply(event, "Now logging server events to this channel")
        saveChannels(channels)
    }).defaultPermissions(1)

    new Command("unsetLogChannel", [] as CommandArg[], { event ->
        channels.remove(event.guild.idLong)
        messagesutil.reply(event, "No longer logging events in this server")
        saveChannels(channels)
    }).defaultPermissions(1)

    // Example event addition
    on(channels, EventType.GuildMessageUpdateEvent, {
        it.message.author.asTag + " has edited message `" + it.message.idLong + "`"
    })
}

static void on(HashMap<Long, Long> channels, EventType type, Function<GenericGuildMemberEvent, String> function) {
    Events.on(type, {
        if (channels.containsKey(it.guild.idLong)) {
            it.guild.getTextChannelById(channels.get(it.guild.idLong)).sendMessage(messagesutil.filter(function.apply(it))).queue()
        }
    })
}

static void saveChannels(HashMap<Long, Long> channels) {
    FileWriter writer = new FileWriter(getFile())
    Files.yaml.dump(channels, writer)
    writer.close()
}

static File getFile() {
    return new File(Files.modules, "serverlog.yaml")
}