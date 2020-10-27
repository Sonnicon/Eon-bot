package sonnicon.eonbot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import sonnicon.eonbot.core.Config
import sonnicon.eonbot.core.EventReceiver
import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.util.Files

class Eonbot {
    static JDA jda
    static Config config

    static void main(String[] args) {
        Files.init()
        Modules.init()
        Commands.init()

        jda = new JDABuilder(config.getToken()).build()
        jda.addEventListener(new EventReceiver())

        if (Files.fileModule("startup").exists())
            Modules.loadModule("startup")

        jda.awaitReady()
    }
}