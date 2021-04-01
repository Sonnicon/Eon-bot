package sonnicon.eonbot.command.CmdArgs

abstract class CmdArg {
    String name

    abstract boolean collect(String input, Map<String, Object> parsed)
}
