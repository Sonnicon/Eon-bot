package sonnicon.eonbot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import sonnicon.eonbot.core.Config
import sonnicon.eonbot.core.EventHandler
import sonnicon.eonbot.core.Modules

class Eonbot {
    static JDA jda
    static Config config = Config.getConfig()
    static Modules modules = new Modules()

    static void main(String[] args) {

        jda = new JDABuilder(config.getToken())
                .addEventListeners(new EventHandler())
                .build()
    }
}