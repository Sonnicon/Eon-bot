import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.util.Files
import java.time.LocalTime;

import static messagesutil

static void main(arg) {
    final HashMap<Integer, String> states = [
            0 : "Startup",
            1 : "Pregame",
            2 : "Setting Up",
            3 : "Playing",
            4 : "Finished"
    ]

    Commands commands = new Commands()

    commands.newCommand("tgstation", { event, args ->
        String key
        if (args.size() == 0) {
            key = "terry"
        }else{
            key = args.get(0)
        }

        URL url = new URL("https://tgstation13.org/dynamicimages/serverinfo.json")
        HttpURLConnection con = url.openConnection()
        String s = readStream(con.getInputStream())
        // yaml has problems with some things so this is necessary
        s = s.replaceAll("[\"]", "'")
        HashMap<String, ?> result = Files.yaml.load(s)

        if(!result.containsKey(key)){
            messagesutil.reply(event, "Server " + target + " not found")
            return
        }
        result = result.get(key)

        messagesutil.embed("/tg/station " + result.get("serverdata").get("servername"))
        messagesutil.embedDescription("Map: " + result.get("map_name") +
                "\nTime: " + LocalTime.MIN.plusSeconds(result.get("round_duration")).toString() +
                "\nPlayers: " + result.get("players") + "/" + result.get("soft_popcap") +
                "\nState: " + states.get(result.get("gamestate")))
        messagesutil.embedFooter(new Date().toString())
        messagesutil.replyEmbed(event)
    })
}

static String readStream(InputStream stream) {
    BufferedInputStream bis = new BufferedInputStream(stream)
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    int result = bis.read()
    while (result != -1) {
        buf.write((byte) result)
        result = bis.read()
    }
    return buf.toString("UTF-8")
}