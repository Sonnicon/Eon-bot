import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.util.Commands

import java.util.regex.Pattern

import static messagesutil

static void main(arg) {
    Commands commands = new Commands()

    commands.newCommand("load", { event, args ->
        args.each {
            if(verify(it))
                messagesutil.reply(event, (Modules.loadModule(it) ? "Loaded" : "Could not load") + " module " + it)
        }
    }).defaultPermissions(2)

    commands.newCommand("unload", { event, args ->
        args.each {
            if(verify(it))
                messagesutil.reply(event, (Modules.unloadModule(it) ? "Unloaded" : "Could not unload") + " module " + it)
        }
    }).defaultPermissions(2)

    commands.newCommand("loaded", { event, args ->
        messagesutil.reply(event, "Loaded modules: " + Modules.moduleMap.keySet().join(" "))
    }).defaultPermissions(2)
}

static boolean verify(String name){
    return Pattern.matches("^[a-zA-Z0-9]+\$", name)
}