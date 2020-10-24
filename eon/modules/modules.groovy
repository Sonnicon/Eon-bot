import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.core.Modules

static void main(arg){
    commands = new Commands()

    commands.newCommand("load",  { event, args ->
        args.each {
            event.channel.sendMessage((Modules.loadModule(it) ? "Loaded" : "Could not load") + " module "+ it).queue()
        }
    })

    commands.newCommand("unload",  { event, args ->
        args.each {
            event.channel.sendMessage((Modules.unloadModule(it) ? "Unloaded" : "Could not unload") + " module "+ it).queue()
        }
    })

    commands.newCommand("loaded",  { event, args ->
        event.channel.sendMessage("Loaded modules: " + Modules.moduleMap.keySet().join(" ")).queue()
    })
}