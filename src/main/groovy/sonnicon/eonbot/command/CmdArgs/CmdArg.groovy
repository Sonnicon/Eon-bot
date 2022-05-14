package sonnicon.eonbot.command.CmdArgs

import sonnicon.eonbot.command.CmdResponse
import sonnicon.eonbot.type.MessageProxy

abstract class CmdArg {
    String name

    void collect(CmdResponse response, List<String> data, Map<String, Object> parsed, MessageProxy message) {
        if (!collect(data.remove(0), parsed)) {
            response.set(CmdResponse.CmdResponseType.illegalArg)
        }
    }

    protected abstract boolean collect(String input, Map<String, Object> parsed)
}
