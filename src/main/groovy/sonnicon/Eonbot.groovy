package sonnicon

import net.dv8tion.jda.api.JDABuilder
import sonnicon.core.Config
import sonnicon.core.EventReceiver
import sonnicon.core.Modules
import sonnicon.util.Commands
import sonnicon.util.Files

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