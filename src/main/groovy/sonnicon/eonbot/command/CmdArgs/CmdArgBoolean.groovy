package sonnicon.eonbot.command.CmdArgs

/**
 * A CmdArg for parsing true/false inputs.
 */
class CmdArgBoolean extends CmdArg {
    /**
     * Valid strings for TRUE values. Lowercase.
     */
    Set<String> yes = ["yes", "true", "1"]
    /**
     * Valid strings for FALSE values. Lowercase.
     */
    Set<String> no = ["no", "false", "0"]

    protected boolean collect(String input, Map<String, Object> parsed) {
        input = input.toLowerCase()
        boolean y = yes.contains(input)
        if (y || no.contains(input)) {
            // Being here means the token is either in yes or no
            parsed.put(name, y)
            return true
        }
        // Token not recognised
        false
    }
}
