package sonnicon.eonbot.command

import sonnicon.eonbot.command.CmdArgs.CmdArg
import sonnicon.eonbot.command.CmdArgs.CmdArgString

class CmdNode {
    CmdArg child
    CmdNode next
    Closure executor
    boolean required

    static Map<String, Class<? extends CmdArg>> childTypes = ["string": CmdArgString.class]

    //slurper stuff
    void setChildData(Map<String, ?> childData) {
        child = childTypes.get(childData.remove("type")) newInstance(childData)
    }

    boolean collect(List<String> data, Map<String, ?> parsed) {
        if (executor) Commands.executor = executor

        if (!data) {
            if (required) {
                //todo missing arg
                return false
            }
            return true
        }

        if (!child.collect(data.remove(0), parsed)) {
            return false
            //todo illegal arg
        } else if (next) {
            return next.collect(data, parsed)
        }

        return true
    }
}
