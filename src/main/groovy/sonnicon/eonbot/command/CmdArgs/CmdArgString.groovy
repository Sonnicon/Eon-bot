package sonnicon.eonbot.command.CmdArgs

import java.util.regex.Matcher
import java.util.regex.Pattern

class CmdArgString extends CmdArg {
    protected Pattern pattern

    void setRegex(String regex){
        pattern = Pattern.compile(regex)
    }

    boolean collect(String input, Map<String, Object> parsed) {
        Matcher matcher = pattern.matcher(input)
        if(!matcher.find(0)) return false
        parsed.put(name, matcher.group())
        return true
    }
}
