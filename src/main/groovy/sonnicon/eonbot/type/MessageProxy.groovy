package sonnicon.eonbot.type

import groovy.util.Proxy
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.emoji.Emoji
import sonnicon.eonbot.util.EmbedUtil

/**
 * Wrapper for Message that adds some useful functions.
 */
class MessageProxy extends Proxy {
    MessageProxy(Message message) {
        super()
        wrap(message)
    }

    /**
     * Get wrapped object.
     * @return
     */
    Message message() {
        adaptee as Message
    }

    /**
     * Send a reply to the message inside an embed.
     * @param content Text to put in the embed
     */
    void reply(CharSequence content) {
        if (message()) {
            // If the message really is a message
            EmbedUtil.setDefaults(this)
            EmbedUtil.addText(content.toString())
            message().getChannel().sendMessageEmbeds(EmbedUtil.embed()).setMessageReference(message()).queue()
            //message().getChannel().sendMessage(content).reference(message()).queue()
        } else {
            // If we're pretending there was a message, respond in console
            println(content)
        }
    }

    void replyEmbed(MessageEmbed embed) {
        if (message()) {
            message().getChannel().sendMessageEmbeds(embed).setMessageReference(message()).queue()
        } else {
            println(embed.dump())
        }
    }

    /**
     * React to the message with a success or a failure.
     * @param success Checkmark if true, cross if false.
     */
    void reactSuccess(boolean success = true) {
        message().addReaction(success ? Emoji.fromUnicode('✅') : Emoji.fromUnicode('❎')).queue()
    }

    /**
     * Get a context string for where the message exists.
     * @return Context of message
     */
    String getContext() {
        if (!message()) null
        "${message().channelType.toString()}~${message().channel.id}"
    }

    /**
     * Override groovy builtin to cast the wrapped message properly.
     * @param target Destination class
     * @return Casted message
     */
    def <T> T asType(Class<T> target) {
        if (Message.isAssignableFrom(target)) {
            message() as T
        } else {
            super.asType(target)
        }
    }
}
