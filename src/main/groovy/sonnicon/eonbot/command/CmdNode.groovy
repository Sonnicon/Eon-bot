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
        child = childTypes.get(childData.remove("type")) newInstance(childData)
    }

    // slurper stuff episode 2
    void setExecutorKey(String executorKey) {
        if (CommandRegistry.executorMap) {
            this.executor = CommandRegistry.executorMap.get(executorKey)
        }
    }

    // slurper stuff: the return of the king
    void setUid(String uid) {
        this.id = (CommandRegistry.name ?: "null") + "-" + uid
    }

    void collect(CmdResponse response, List<String> data, Map<String, ?> parsed, Message message) {
        // check permissions
        if (!Commands.checkPermissions(message, id)) {
            //if (!Commands.checkPermissions(message.author.idLong, message.isFromGuild() ? message.guild.idLong : 0, message.isFromGuild() ? message.member.getRoles() + message.guild.publicRole : null, id)) {
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
