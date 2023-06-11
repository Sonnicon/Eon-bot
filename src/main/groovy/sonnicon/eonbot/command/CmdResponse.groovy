package sonnicon.eonbot.command

/**
 * Response information about the handling of a command.
 */
class CmdResponse {
    /**
     * What the result of the handling was.
     */
    CmdResponseType type = CmdResponseType.success
    /**
     * Handler to be called to execute the command.
     */
    Closure<Boolean> executor

    /**
     * Sets the result of command handling.
     * @param type The result to be set
     * @return The response is returned again
     */
    CmdResponse set(CmdResponseType type) {
        this.type = type
        return this
    }

    /**
     * Sets the executor of the command.
     * @param executor Handler to be executed
     * @return The response is returned again
     */
    CmdResponse set(Closure<Boolean> executor) {
        this.executor = executor
        return this
    }

    /**
     * Possible types of response from handling of a command.
     */
    static enum CmdResponseType {
        /**
         * A required parameter was not provided.
         */
        missingArg,
        /**
         * A provided parameter had an incorrect format.
         */
        illegalArg,
        /**
         * More parameters were provided than expected.
         */
        extraArg,
        /**
         * Permission to execute the command was missing.
         */
        badPermission,
        /**
         * Command is valid to be executed.
         */
        success
    }
}
