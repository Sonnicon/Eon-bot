import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.Eonbot
import net.dv8tion.jda.api.entities.Activity
import sonnicon.eonbot.util.Commands

static void main(arg) {
    String[] modules = ["log", "permissions", "modules", "basiccommands", "markov"]
    modules.each {Modules.loadModule(it)}

    Eonbot.jda.presence.setActivity(Activity.watching(" for " + Commands.commandPrefix))
}