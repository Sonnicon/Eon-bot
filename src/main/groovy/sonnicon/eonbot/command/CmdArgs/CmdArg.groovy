package sonnicon.eonbot.command.CmdArgs

import net.dv8tion.jda.api.entities.Message
import sonnicon.eonbot.command.CmdResponse

abstract class CmdArg {
    String name

    void collect(CmdResponse response, List<String> data, Map<String, Object> parsed, Message message) {
        if (!collect(data.remove(0), parsed)) {
            response.set(CmdResponse.CmdResponseType.illegalArg)
        }
    }

    protected abstract boolean collect(String input, Map<String, Object> parsed)
}
