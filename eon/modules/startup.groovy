import net.dv8tion.jda.api.entities.Activity
import sonnicon.eonbot.Eonbot
import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.util.command.Commands

static void main(arg) {
    String[] modules = ["log", "commandargtypes", "permissions", "modules", "basiccommands", "hastebin"]
    modules.each { Modules.loadModule(it) }

    Eonbot.jda.presence.setActivity(Activity.watching(" for " + Commands.commandPrefix))
}