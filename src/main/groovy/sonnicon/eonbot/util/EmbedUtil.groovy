package sonnicon.eonbot.util

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import sonnicon.eonbot.type.MessageProxy

/**
 * Utilities for building embeds.
 * //todo make it work with multithreading
 */
class EmbedUtil {
    /**
     * EmbedBuilder used for making the embeds.
     */
    static EmbedBuilder embedBuilder = new EmbedBuilder()

    /**
     * Create an embed from the currently set data.
     * @return Resulting embed
     */
    static MessageEmbed embed() {
        embedBuilder.build()
    }

    /**
     * Clears and sets default information for an embed from a message.
     * @param message Message to be defaulted from
     */
    static void setDefaults(MessageProxy message) {
        embedBuilder.clear()
        if (message) {
            setAuthorFooter(message)
        }
    }

    /**
     * Set title of the embed.
     * @param title New title text
     * @param url URL link on the title
     */
    static void setTitle(String title, String url = null) {
        embedBuilder.setTitle(title, url)
    }

    /**
     * Add text to the current embed.
     * @param value Text to be added
     * @param name Field to be added to
     * @param inline Whether text is inline
     */
    static void addText(String value, String name = "", boolean inline = false) {
        embedBuilder.addField(name, value, inline)
    }

    /**
     * Sets footer to author of a message, with avatar image.
     * @param message Message to be used for author.
     */
    static void setAuthorFooter(MessageProxy message) {
        embedBuilder.setFooter(message.getAuthor().asTag, message.getAuthor().avatarUrl)
    }
}
