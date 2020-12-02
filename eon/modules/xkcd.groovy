import messagesutil
import sonnicon.eonbot.util.Files
import sonnicon.eonbot.util.command.Command
import sonnicon.eonbot.util.command.CommandArg
import sonnicon.eonbot.util.command.CommandArgType

static void main(arg) {
    short latest = -1
    Random random = new Random()

    new Command("xkcd", [new CommandArg(CommandArgType.getType("Short"), "Number (0=random)", false)] as CommandArg[],
            { event, arg1 = -1 as Short ->

                // -1=latest 0=random >0=number
                if (arg1 == 0) {
                    if (latest == -1 as short) {
                        latest = getComic(-1 as short).get("num") as short
                    }
                    arg1 = (Short) (random.nextInt(latest - 1) + 1)
                }

                try {
                    HashMap<String, String> resultFiles = getComic(arg1)

                    if (arg1 == -1) {
                        latest = resultFiles.get("num") as short
                    }

                    messagesutil.embed("XKCD: " + resultFiles.get("num"))
                    messagesutil.embedFooter(resultFiles.get("alt"))
                    messagesutil.embedImage(resultFiles.get("img"))
                    messagesutil.replyEmbed(event)
                } catch (FileNotFoundException ex) {
                    messagesutil.reply(event, "XKCD " + arg1 + " not found")
                }
            })
}

static HashMap<String, String> getComic(short target) {
    URL url = new URL("https://xkcd.com/" + ((target == -1) ? "" : (target + "/")) + "info.0.json")
    HttpURLConnection con = url.openConnection()
    return Files.yaml.load(con.getInputStream())
}