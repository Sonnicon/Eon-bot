import net.dv8tion.jda.api.entities.Message
import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.util.Files

import static messagesutil

static void main(arg) {
    final URL url = new URL("https://hastebin.com/documents")
    final byte[] buffer = new byte[1024]

    final Commands commands = new Commands()

    commands.newCommand("hastebin", { event, args ->
        Message.Attachment a
        if(event.message.attachments.size() > 0){
            a = event.message.attachments.get(0)
        }else if(args.size() > 0){
            a = event.channel.retrieveMessageById(args.get(0)).complete().attachments.get(0)
        }

        if(a != null){
            if(!a.image && !a.video && a.size <= 5000000){
                InputStream stream = a.retrieveInputStream().get()
                HttpURLConnection con = url.openConnection()
                con.setDoOutput(true)
                con.setRequestMethod("POST")
                con.setRequestProperty("User-Agent", "Discord bot module")

                OutputStream out = con.getOutputStream()
                int len
                while ((len = stream.read(buffer)) != -1) {
                    out.write(buffer, 0, len)
                }

                messagesutil.embed(a.getFileName())
                messagesutil.embedDescription("https://hastebin.com/" + Files.yaml.load(con.getInputStream()).get("key"))
                messagesutil.replyEmbed(event)
            }
        }
    })
}