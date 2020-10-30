import net.dv8tion.jda.api.events.message.GenericMessageEvent

static void reply(GenericMessageEvent event, String message, Boolean filtered){
    event.channel.sendMessage(filtered ? filter(message) : message).queue()
}

static void reply(GenericMessageEvent event, String message){
    reply(event, message, true)
}

static String filter(String message){
    return message.replaceAll("[@]", '＠')
}