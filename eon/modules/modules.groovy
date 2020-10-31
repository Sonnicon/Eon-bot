import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.util.Files

import static messagesutil

static void main(arg) {
    Commands commands = new Commands()

    commands.newCommand("load", { event, args ->
        args.each {
            if(Files.verify(it))
                messagesutil.reply(event, (Modules.loadModule(it) ? "Loaded" : "Could not load") + " module `" + it + "`")
        }
    }).defaultPermissions(2)

    commands.newCommand("unload", { event, args ->
        args.each {
            if(Files.verify(it))
                messagesutil.reply(event, (Modules.unloadModule(it) ? "Unloaded" : "Could not unload") + " module `" + it + "`")
        }
    }).defaultPermissions(2)

    commands.newCommand("loaded", { event, args ->
        messagesutil.reply(event, "Loaded modules: `" + Modules.moduleMap.keySet().join("` `") + "`")
    }).defaultPermissions(2)
}