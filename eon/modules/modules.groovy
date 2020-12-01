import messagesutil
import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.util.Files
import sonnicon.eonbot.util.command.Command
import sonnicon.eonbot.util.command.CommandArg
import sonnicon.eonbot.util.command.CommandArgType

static void main(arg) {
    new Command("load", [new CommandArg(CommandArgType.getType("String"), "Module")]
            as CommandArg[], {
        event, arg1 ->
            if (Files.verify(arg1))
                messagesutil.reply(event, (Modules.loadModule(arg1) ? "Loaded" : "Could not load") + " module `" + arg1 + "`")
    }).defaultPermissions(2)

    new Command("unload", [new CommandArg(CommandArgType.getType("String"), "Module")]
            as CommandArg[], {
        event, arg1 ->
            if (Files.verify(arg1))
                messagesutil.reply(event, (Modules.unloadModule(arg1) ? "Unloaded" : "Could not unload") + " module `" + arg1 + "`")
    }).defaultPermissions(2)

    new Command("loaded", [] as CommandArg[], { event ->
        messagesutil.reply(event, "Loaded modules: `" + Modules.moduleMap.keySet().join("` `") + "`")
    }).defaultPermissions(2)
}