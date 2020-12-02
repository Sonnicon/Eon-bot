import messagesutil
import sonnicon.eonbot.util.Files
import sonnicon.eonbot.util.command.Command
import sonnicon.eonbot.util.command.CommandArg
import sonnicon.eonbot.util.command.CommandArgType

import java.time.LocalTime

static void main(arg) {
    final HashMap<Integer, String> states = [
            0: "Startup",
            1: "Pregame",
            2: "Setting Up",
            3: "Playing",
            4: "Finished"
    ]

    new Command("tgstation", [new CommandArg(CommandArgType.getType("String"), "Server", false)] as CommandArg[],
            { event, arg1 = "terry" ->

                URL url = new URL("https://tgstation13.org/dynamicimages/serverinfo.json")
                HttpURLConnection con = url.openConnection()
                String s = readStream(con.getInputStream())
                // yaml has problems with some things so this is necessary
                s = s.replaceAll("[\"]", "'")
                HashMap<String, ?> result = Files.yaml.load(s)

                if (!result.containsKey(arg1)) {
                    messagesutil.reply(event, "Server " + target + " not found")
                    return
                }
                result = result.get(arg1)

                messagesutil.embed("/tg/station " + result.get("serverdata").get("servername"))
                messagesutil.embedDescription(
                        result.get("error") ?
                                "Server error (restarting?)" :
                                ("Map: " + result.get("map_name") +
                                        "\nTime: " + LocalTime.MIN.plusSeconds(result.get("round_duration")).toString() +
                                        "\nPlayers: " + result.get("players") + "/" + result.get("soft_popcap") +
                                        "\nState: " + states.get(result.get("gamestate")) +
                                        "\nShuttle: " + result.get("shuttle_mode").toUpperCase() + " ("
                                        + LocalTime.MIN.plusSeconds(result.get("shuttle_timer")).toString() + ")")
                )
                messagesutil.embedFooter(new Date(result.get("cachetime")).toString())
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