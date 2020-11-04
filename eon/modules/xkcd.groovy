import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.util.Files

import static messagesutil

static void main(arg) {
    Commands commands = new Commands()
    short latest = -1
    Random random = new Random()

    commands.newCommand("xkcd", { event, args ->
        if (args.size() == 0) {
            messagesutil.reply(event, "`random` `latest` `<number>`")
            return
        }

        Short target = -1

        if(args[0].equals("random")){
            if(latest == -1 as short){
                latest = getComic(-1 as short).get("num") as short
            }
            target = (Short) (random.nextInt(latest - 1) + 1)
        }else if(!args[0].equals("latest")){
            target = Short.parseShort(args[0])
        }

        try {
            HashMap<String, String> resultFiles = getComic(target)

            if(target == -1){
                latest = resultFiles.get("num") as short
            }

            messagesutil.embed("XKCD: " + resultFiles.get("num"))
            messagesutil.embedFooter(resultFiles.get("alt"))
            messagesutil.embedImage(resultFiles.get("img"))
            messagesutil.replyEmbed(event)
        }catch(FileNotFoundException ex){
            messagesutil.reply(event, "XKCD " + target + " not found")
        }
    })
}

static HashMap<String, String> getComic(short target){
    URL url = new URL("https://xkcd.com/" + ((target == -1) ? "" : (target + "/")) + "info.0.json")
    HttpURLConnection con = url.openConnection()
    return Files.yaml.load(con.getInputStream())
}