package sonnicon.eonbot.type

import groovy.util.Proxy
import net.dv8tion.jda.api.entities.Message
import sonnicon.eonbot.util.EmbedUtil

class MessageProxy extends Proxy {
    MessageProxy(Message message) {
        super()
        wrap(message)
    }

    Message message() {
        adaptee as Message
    }

    void reply(CharSequence content) {
        if (message()) {
            EmbedUtil.setDefaults(this)
            EmbedUtil.addText(content.toString())
            message().getChannel().sendMessageEmbeds(EmbedUtil.embed()).reference(message()).queue()
            //message().getChannel().sendMessage(content).reference(message()).queue()
        } else {
            println(content)
        }
    }

    void reactSuccess(boolean success = true) {
        message().addReaction(success ? '✅' : '❎').queue()
    }

    String getContext() {
        if (!message()) null
        "${message().channelType.toString()}~${message().channel.id}"
    }

    def <T> T asType(Class<T> target) {
        if (Message.isAssignableFrom(target)) {
            message() as T
        } else {
            super.asType(target)
        }
    }
}
