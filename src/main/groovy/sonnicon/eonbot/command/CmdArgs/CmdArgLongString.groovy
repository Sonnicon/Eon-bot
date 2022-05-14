package sonnicon.eonbot.command.CmdArgs

import sonnicon.eonbot.command.CmdResponse
import sonnicon.eonbot.type.MessageProxy

class CmdArgLongString extends CmdArgString {
    void collect(CmdResponse response, List<String> data, Map<String, Object> parsed, MessageProxy message) {
        if (!collect(data.join(" "), parsed)) {
            response.set(CmdResponse.CmdResponseType.illegalArg)
        }
        data.clear()
    }
}
