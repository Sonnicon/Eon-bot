import net.dv8tion.jda.api.events.session.ReadyEvent
import sonnicon.eonbot.core.EventHandler
import sonnicon.eonbot.core.FileIO
import sonnicon.eonbot.type.ExecutorFunc
import sonnicon.eonbot.type.MessageProxy
import sonnicon.eonbot.type.ModuleBase
import sonnicon.eonbot.util.EmbedUtil

import java.time.LocalTime

// port of module from v1
class ss13 extends ModuleBase {
    final HashMap<Integer, String> states = [
            0: "Startup",
            1: "Pregame",
            2: "Setting Up",
            3: "Playing",
            4: "Finished"
    ]

    void load(String context) {
        Closure readyEventClosure
        readyEventClosure = { ReadyEvent it ->
            println "Bot connected and ready!"
            EventHandler.remove(ReadyEvent.class, readyEventClosure)
        }
        EventHandler.register(ReadyEvent.class, readyEventClosure)
    }

    @ExecutorFunc("tgstation")
    boolean tgstation(Map<String, ?> data, MessageProxy message) {
        String target = data.getOrDefault("server", "terry")
        URL url = new URL("https://tgstation13.org/dynamicimages/serverinfo.json")
        HttpURLConnection con = url.openConnection() as HttpURLConnection
        String s = readStream(con.getInputStream())
        // yaml has problems with some things so this is necessary
        s = s.replaceAll('"', "'")
        HashMap<String, ?> result = FileIO.yamlSlurper.parseText(s) as HashMap<String, ?>

        if (!result.containsKey(target)) {
            message.reply("Server '${target}' not found.")
            return false
        }
        result = result.get(target) as HashMap<String, ?>
        EmbedUtil.setDefaults(message)
        EmbedUtil.setTitle("TgStation ${result.get("serverdata").get("servername")}")
        if (result.get("error")) {
            EmbedUtil.addText("Error")
        } else {
            EmbedUtil.addText(result.get("map_name") as String, "Map")
            EmbedUtil.addText(LocalTime.MIN.plusSeconds(result.get("round_duration") as long).toString(), "Time")
            EmbedUtil.addText("${result.get('players')}/${result.get('soft_popcap')}", "Players")
            EmbedUtil.addText(states.get(result.get("gamestate")), "State")
            EmbedUtil.addText("${result.get('shuttle_mode').toUpperCase()} " +
                    "(${LocalTime.MIN.plusSeconds(result.get("shuttle_timer") as long).toString()})", "Shuttle")
        }
        message.replyEmbed(EmbedUtil.embed())
        true
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
}