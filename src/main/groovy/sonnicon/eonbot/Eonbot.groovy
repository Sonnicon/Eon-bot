package sonnicon.eonbot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import sonnicon.eonbot.command.Commands
import sonnicon.eonbot.core.Config
import sonnicon.eonbot.core.Database
import sonnicon.eonbot.core.EventHandler
import sonnicon.eonbot.core.Modules

class Eonbot {
    static JDA jda
    static Config config = Config.getConfig()
    static Database permissions = new Database()
    static Modules modules = new Modules()
    static Commands commands = new Commands()

    static void main(String[] args) {
        jda = JDABuilder.createDefault(Eonbot.config.getToken())
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new EventHandler())
                .build()
    }
}