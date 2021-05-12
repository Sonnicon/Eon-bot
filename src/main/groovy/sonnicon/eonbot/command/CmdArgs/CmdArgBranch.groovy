package sonnicon.eonbot.command.CmdArgs

import net.dv8tion.jda.api.entities.Message
import sonnicon.eonbot.command.CmdNode
import sonnicon.eonbot.command.CmdResponse


class CmdArgBranch extends CmdArg {
    HashMap<String, CmdNode> childs = []

    void setChilds(Map<String, CmdNode> childs) {
        childs.each { this.childs.put(it.key, it.value as CmdNode) }
    }

    void collect(CmdResponse response, List<String> input, Map<String, Object> parsed, Message message) {
        String key = input.remove(0)
        if (childs.containsKey(key)) {
            parsed.put(name, key)
            childs.get(key).collect(response, input, parsed, message)
        } else {
            response.set(CmdResponse.CmdResponseType.illegalArg)
        }
    }

    protected boolean collect(String input, Map<String, Object> parsed) {
        return false
    }
}
