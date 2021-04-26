package sonnicon.eonbot.command

import sonnicon.eonbot.command.CmdArgs.CmdArg
import sonnicon.eonbot.command.CmdArgs.CmdArgBranch
import sonnicon.eonbot.command.CmdArgs.CmdArgString

class CmdNode {
    CmdArg child
    CmdNode next
    Closure executor
    boolean required = true

    static Map<String, Class<? extends CmdArg>> childTypes = [
            "string": CmdArgString.class,
            "branch": CmdArgBranch.class
    ]

    //slurper stuff
    void setChildData(Map<String, ?> childData) {
        child = childTypes.get(childData.remove("type")) newInstance(childData)
    }

    void setExecutorKey(String executorKey) {
        if (CommandRegistry.executorMap) {
            this.executor = CommandRegistry.executorMap.get(executorKey)
        }
    }

    void collect(CmdResponse response, List<String> data, Map<String, ?> parsed) {
        if (executor) {
            response.set(executor)
        }

        if (child) {
            if (!data) {
                if (required) {
                    response.set(CmdResponse.CmdResponseType.missingArg)
                }
                return
            }
            child.collect(response, data, parsed)
            if (response.type == CmdResponse.CmdResponseType.illegalArg) {
                return
            }
            if (next) {
                next.collect(response, data, parsed)
            } else if (data) {
                response.set(CmdResponse.CmdResponseType.extraArg)
            }
        }
    }
}
