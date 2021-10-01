package sonnicon.eonbot.command

class CmdResponse {
    CmdResponseType type = CmdResponseType.success
    Closure<Boolean> executor

    CmdResponse set(CmdResponseType type) {
        this.type = type
        return this
    }

    CmdResponse set(Closure<Boolean> executor) {
        this.executor = executor
        return this
    }

    static enum CmdResponseType {
        missingArg,
        illegalArg,
        extraArg,
        badPermission,
        success
    }
}
