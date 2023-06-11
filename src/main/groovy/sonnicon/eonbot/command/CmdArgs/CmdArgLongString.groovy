package sonnicon.eonbot.command.CmdArgs

import sonnicon.eonbot.command.CmdResponse
import sonnicon.eonbot.type.MessageProxy

/**
 * A CmdArg to parse a string that isn't delimited by quotes.
 */
class CmdArgLongString extends CmdArgString {
    void collect(CmdResponse response, List<String> data, Map<String, Object> parsed, MessageProxy message) {
        // Join all the subsequent tokens with spaces, and parse that as a string
        if (!collect(data.join(" "), parsed)) {
            response.set(CmdResponse.CmdResponseType.illegalArg)
        }
        // We used up all the remaining tokens
        data.clear()
    }
}
