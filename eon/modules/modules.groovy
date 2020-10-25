import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.type.PermissionLevel

static void main(arg) {
    Commands commands = new Commands()

    commands.newCommand("load", { event, args ->
        args.each {
            event.channel.sendMessage((Modules.loadModule(it) ? "Loaded" : "Could not load") + " module " + it).queue()
        }
    }).setPermissionLevel(PermissionLevel.operators)

    commands.newCommand("unload", { event, args ->
        args.each {
            event.channel.sendMessage((Modules.unloadModule(it) ? "Unloaded" : "Could not unload") + " module " + it).queue()
        }
    }).setPermissionLevel(PermissionLevel.operators)

    commands.newCommand("loaded", { event, args ->
        event.channel.sendMessage("Loaded modules: " + Modules.moduleMap.keySet().join(" ")).queue()
    })
}