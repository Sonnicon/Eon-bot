import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

import java.awt.Color

class messagesutil {
    static EmbedBuilder builder = new EmbedBuilder()

    static void reply(GenericMessageEvent event, String message, Boolean filtered) {
        event.channel.sendMessage(filtered ? filter(message) : message).queue()
    }

    static void reply(GenericMessageEvent event, String message) {
        reply(event, message, true)
    }

    static void embed(String title){
        embed(title, null)
    }

    static void embed(String title, String url){
        builder.clear()
        builder.setTitle(title, url)
    }

    static void embedColor(Color color){
        builder.color = color
    }

    static void embedImage(String image){
        builder.image = image
    }

    static void embedDescription(CharSequence description){
        builder.description = description
    }

    static void embedFooter(String footer){
        builder.footer = footer
    }

    static void replyEmbed(MessageReceivedEvent event){
        event.channel.sendMessage(builder.build()).queue()
    }

    static String filter(String message) {
        return message.replaceAll("[@]", '＠')
    }
}