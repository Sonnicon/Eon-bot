package sonnicon.eonbot.command.CmdArgs

import sonnicon.eonbot.command.CmdResponse

class CmdArgLongString extends CmdArgString {
    @Override
    void collect(CmdResponse response, List<String> data, Map<String, Object> parsed) {
        if (!collect(data.join(" "), parsed)) {
            response.set(CmdResponse.CmdResponseType.illegalArg)
        }
        data.clear()
    }
}
