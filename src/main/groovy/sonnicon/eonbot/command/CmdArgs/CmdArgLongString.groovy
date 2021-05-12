package sonnicon.eonbot.command.CmdArgs

import net.dv8tion.jda.api.entities.Message
import sonnicon.eonbot.command.CmdResponse

class CmdArgLongString extends CmdArgString {
    void collect(CmdResponse response, List<String> data, Map<String, Object> parsed, Message message) {
        if (!collect(data.join(" "), parsed)) {
            response.set(CmdResponse.CmdResponseType.illegalArg)
        }
        data.clear()
    }
}
