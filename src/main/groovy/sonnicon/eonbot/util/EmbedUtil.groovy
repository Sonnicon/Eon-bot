package sonnicon.eonbot.util

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import sonnicon.eonbot.type.MessageProxy

class EmbedUtil {
    static EmbedBuilder embedBuilder = new EmbedBuilder()

    static MessageEmbed embed() {
        embedBuilder.build()
    }

    static void setDefaults(MessageProxy message) {
        embedBuilder.clear()
        setAuthorFooter(message)
    }

    static void addText(String value, String name = "", boolean inline = false) {
        embedBuilder.addField(name, value, inline)
    }

    static void setAuthorFooter(MessageProxy message) {
        embedBuilder.setFooter(message.getAuthor().asTag, message.getAuthor().avatarUrl)
    }
}
