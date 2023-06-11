package sonnicon.eonbot.command.CmdArgs

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A CmdArg to parse user mentions and IDs into IDs.
 */
class CmdArgUser extends CmdArg {
    protected Pattern pattern = Pattern.compile("(^\\d+\$)|(?<=^<@!)(\\d+)(?=>\$)")

    protected boolean collect(String input, Map<String, Object> parsed) {
        // Similar to CmdArgString parsing
        Matcher matcher = pattern.matcher(input)
        if (matcher.find()) {
            // Except we also make it a long
            parsed.put(name, Long.parseLong(matcher.group()))
            return true
        }
        false
    }
}
