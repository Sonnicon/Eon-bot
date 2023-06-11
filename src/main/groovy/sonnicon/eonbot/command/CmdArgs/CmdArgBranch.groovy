package sonnicon.eonbot.command.CmdArgs

import sonnicon.eonbot.command.CmdNode
import sonnicon.eonbot.command.CmdResponse
import sonnicon.eonbot.type.MessageProxy

/**
 * A CmdArg to split the node chain into a node tree based on Strings.
 */
class CmdArgBranch extends CmdArg {
    /**
     * Map of parameter values and command nodes of children to branch to.
     */
    HashMap<String, CmdNode> childs = []

    // Slurper stuff
    void setChilds(Map<String, CmdNode> childs) {
        childs.each { this.childs.put(it.key, it.value as CmdNode) }
    }

    void collect(CmdResponse response, List<String> input, Map<String, Object> parsed, MessageProxy message) {
        String key = input.remove(0)
        if (childs.containsKey(key)) {
            parsed.put(name, key)
            // Invoke the selected child
            childs.get(key).collect(response, input, parsed, message)
        } else {
            // We didn't have a valid selection
            response.set(CmdResponse.CmdResponseType.illegalArg)
        }
    }

    protected boolean collect(String input, Map<String, Object> parsed) {
        return false
    }
}
