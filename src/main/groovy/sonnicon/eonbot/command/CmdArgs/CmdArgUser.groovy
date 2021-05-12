package sonnicon.eonbot.command.CmdArgs

import java.util.regex.Matcher
import java.util.regex.Pattern

class CmdArgUser extends CmdArg {
    protected Pattern pattern = Pattern.compile("(^\\d+\$)|(?<=^<@!)(\\d+)(?=>\$)")

    protected boolean collect(String input, Map<String, Object> parsed) {
        Matcher matcher = pattern.matcher(input)
        if (matcher.find()) {
            parsed.put(name, Long.parseLong(matcher.group()))
            return true
        }
        false
    }
}
