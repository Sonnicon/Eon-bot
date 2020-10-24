package sonnicon.eonbot

import net.dv8tion.jda.api.JDABuilder
import sonnicon.eonbot.core.Config
import sonnicon.eonbot.core.EventReceiver
import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.util.Files

class Eonbot {
    static jda

    static void main(String[] args) {
        Files.init()
        Modules.init()
        Commands.init()

        jda = new JDABuilder(Config.getToken()).build()
        jda.addEventListener(new EventReceiver())
        jda.awaitReady()

        if (Files.fileModule("startup").exists())
            Modules.loadModule("startup")
    }
}