package sonnicon.eonbot.command.CmdArgs

import sonnicon.eonbot.command.CmdResponse
import sonnicon.eonbot.type.MessageProxy

/**
 * A parser element for nodes in the command tree.
 * Loaded from the YAML.
 */
abstract class CmdArg {
    /**
     * Name of the argument, used for storing data in the parsed map easily.
     */
    String name

    /**
     * A more powerful version of the collect(String, Map) function with additional parameters.
     * @param response Response of the command node tree handling
     * @param data List of tokens of the command
     * @param parsed Map of data parsed by other nodes
     * @param message Message which caused the command parsing
     */
    void collect(CmdResponse response, List<String> data, Map<String, Object> parsed, MessageProxy message) {
        // Remove the first token so we don't process the same token multiple times
        if (!collect(data.remove(0), parsed)) {
            response.set(CmdResponse.CmdResponseType.illegalArg)
        }
    }

    /**
     * Defines the parsing behavior of the argument.
     * @param input Token to be parsed
     * @param parsed Map of parsed data, ey is usually the name property of the arg
     * @return True if the argument was parsed successfully
     */
    protected abstract boolean collect(String input, Map<String, Object> parsed)
}
