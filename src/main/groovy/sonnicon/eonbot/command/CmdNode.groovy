package sonnicon.eonbot.command

import sonnicon.eonbot.command.CmdArgs.*
import sonnicon.eonbot.type.MessageProxy

/**
 * An entry in the command execution chain. Doesn't perform any parsing itself, and only forms the chain.
 */
class CmdNode {
    /**
     * Command parsing information for this node.
     */
    CmdArg child
    /**
     * Next node to be evaluated in the chain.
     */
    CmdNode next
    /**
     * Handler to be executed if parsing is successful.
     * Only the last executor in a node chain is executed.
     */
    Closure<Boolean> executor
    /**
     * Whether this is an optional parameter or required.
     */
    boolean required = true
    /**
     * Node ID, used for permissions for specific nodes.
     */
    String id

    /**
     * Argument type definitions for loading nodes from YAML.
      */
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

    /**
     * Parsing function for reading a command using the node chain.
     * @param response Response for collecting results of the operation
     * @param data List of tokens of the command
     * @param parsed Map of data that has been, and will be, parsed from the tokens by nodes
     * @param message Message from which parsing originated
     */
    void collect(CmdResponse response, List<String> data, Map<String, ?> parsed, MessageProxy message) {
        // Check permissions
        if (!Commands.checkPermissions(message, id)) {
            response.set(CmdResponse.CmdResponseType.badPermission)
            return
        }

        // Set the executor, so only last in chain is kept
        if (executor) {
            response.set(executor)
        }

        // Parse args and recurse
        if (child) {
            // We expected a token but didn't find it
            if (!data) {
                if (required) {
                    response.set(CmdResponse.CmdResponseType.missingArg)
                }
                // It's okay if we didn't need it
                return
            }
            // Parse the token
            child.collect(response, data, parsed, message)
            // Child wasn't happy
            if (response.type == CmdResponse.CmdResponseType.illegalArg) {
                return
            }
            // Recurse down the tree
            if (next) {
                next.collect(response, data, parsed, message)
            }
        }
    }
}
