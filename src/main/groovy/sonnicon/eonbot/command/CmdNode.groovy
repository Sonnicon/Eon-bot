package sonnicon.eonbot.command

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import org.bson.Document
import org.bson.types.ObjectId
import sonnicon.eonbot.command.CmdArgs.*
import sonnicon.eonbot.core.Database

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
        if (!message) return
        // check permissions
        if (!checkPermissions(message.author.idLong, message.isFromGuild() ? message.guild.idLong : 0, message.isFromGuild() ? message.member.getRoles() + message.guild.publicRole : null)) {
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

    protected boolean checkPermissions(long author, long guild, List<Role> roles) {
        // Global
        Document x = Database.getUser(author, 0)
        if (x) {
            // User global
            if ((x.get("permissions") as Map).containsKey(id)) {
                return (x.get("permissions") as Map).get(id)
            }

            // Group
            if (x.containsKey("groups")) {
                for (ObjectId group : x.get("groups")) {
                    x = Database.getGroupById(group)
                    if (x && (x.get("permissions") as Map).containsKey(id)) {
                        return (x.get("permissions") as Map).get(id)
                    }
                }
            }

            // Everyone group
            x = Database.getGroup("everyone")
            if (x.containsKey("permissions")) {
                Map permissions = x.get("permissions")
                if (permissions.containsKey(id)) {
                    return permissions.get(id)
                }
            }
        }

        // User guild
        x = Database.getUser(author, guild)
        if (x && (x.get("permissions") as Map).containsKey(id)) {
            return (x.get("permissions") as Map).get(id)
        }

        // Role guild
        if (guild != 0) {
            for (Role role : roles) {
                x = Database.getRole(role.idLong)
                if (x && (x.get("permissions") as Map).containsKey(id)) {
                    return (x.get("permissions") as Map).get(id)
                }
            }
        }
        true
    }
}
