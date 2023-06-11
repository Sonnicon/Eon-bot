package sonnicon.eonbot.command.CmdArgs

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A CmdArg for parsing strings.
 */
class CmdArgString extends CmdArg {
    /**
     * A pattern for validating input strings.
     */
    protected Pattern pattern
    /**
     * Whether to parse the string as lowercase.
     */
    protected boolean lower = false

    /**
     * Set the filter of the argument to a new regular expression.
     * Invoked by slurper.
     * @param regex Regex string to be used for validating input strings.
     */
    void setRegex(String regex) {
        pattern = Pattern.compile(regex)
    }

    protected boolean collect(String input, Map<String, Object> parsed) {
        // Ensure matching
        Matcher matcher = pattern.matcher(lower ? input.toLowerCase() : input)
        if (!matcher.find(0)) return false
        // Add to data
        parsed.put(name, matcher.group())
        return true
    }
}
