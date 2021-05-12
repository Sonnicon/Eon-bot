package sonnicon.eonbot.command.CmdArgs

class CmdArgBoolean extends CmdArg {
    Set<String> yes = ["yes", "true", "1"]
    Set<String> no = ["no", "false", "0"]

    protected boolean collect(String input, Map<String, Object> parsed) {
        input = input.toLowerCase()
        boolean y = yes.contains(input)
        if (y || no.contains(input)) {
            parsed.put(name, y)
            return true
        }
        false
    }
}
