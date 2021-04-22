package sonnicon.eonbot.command

import sonnicon.eonbot.command.CmdArgs.CmdArg
import sonnicon.eonbot.command.CmdArgs.CmdArgString

class CmdNode {
    CmdArg child
    CmdNode next
    Closure executor
    boolean required = true

    static Map<String, Class<? extends CmdArg>> childTypes = ["string": CmdArgString.class]

    //slurper stuff
    void setChildData(Map<String, ?> childData) {
        child = childTypes.get(childData.remove("type")) newInstance(childData)
    }

    void collect(CmdResponse response, List<String> data, Map<String, ?> parsed) {
        response.set(executor)

        if (child) {
            if (!data) {
                if (required) {
                    response.set(CmdResponse.CmdResponseType.missingArg)
                }
                return
            }
            if (!child.collect(data.remove(0), parsed)) {
                response.set(CmdResponse.CmdResponseType.illegalArg)
            } else if (next) {
                next.collect(response, data, parsed)
            }
        }
    }
}
