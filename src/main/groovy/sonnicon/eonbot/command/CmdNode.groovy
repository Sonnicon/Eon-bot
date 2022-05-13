package sonnicon.eonbot.command

import net.dv8tion.jda.api.entities.Message
import sonnicon.eonbot.command.CmdArgs.*

class CmdNode {
    // core node data
    CmdArg child
    CmdNode next
    Closure<Boolean> executor
    boolean required = true
    String id

    // arg definitions for loading yaml
    static Map<String, Class<? extends CmdArg>> childTypes = [
            "string"    : CmdArgString.class,
            "longstring": CmdArgLongString.class,
            "branch"    : CmdArgBranch.class,
            "user"      : CmdArgUser.class,
            "role"      : CmdArgRole.class,
            "boolean"   : CmdArgBoolean.class
    ]

    // slurper stuff
    void setChildData(Map<String, ?> childData) {
        // setProperty is a mess, this is much easier and cleaner
        String type = childData.remove("type")
        child = childTypes.get(type)?.newInstance(childData)
        childData.put("type", type)
    }

    // slurper stuff 2
    void setUid(String uid) {
        this.id = "${CommandRegistry.name ?: 'null'}-$uid"
        this.executor = CommandRegistry.executorMap?.get(uid)
    }

    void collect(CmdResponse response, List<String> data, Map<String, ?> parsed, Message message) {
        // check permissions
        if (!Commands.checkPermissions(message, id)) {
            response.set(CmdResponse.CmdResponseType.badPermission)
            return
        }

        // parse args and recurse
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
            child.collect(response, data, parsed, message)
            if (response.type == CmdResponse.CmdResponseType.illegalArg) {
                return
            }
            if (next) {
                next.collect(response, data, parsed, message)
            }
        }
    }
}
