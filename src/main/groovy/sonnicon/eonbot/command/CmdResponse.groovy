package sonnicon.eonbot.command

class CmdResponse {
    CmdResponseType type = CmdResponseType.success
    Closure executor

    CmdResponse set(CmdResponseType type) {
        this.type = type
        return this
    }

    CmdResponse set(Closure executor) {
        this.executor = executor
        return this
    }

    static enum CmdResponseType {
        missingArg,
        illegalArg,
        extraArg,
        success
    }
}
