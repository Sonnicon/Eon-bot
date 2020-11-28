package sonnicon.eonbot.util.command

class CommandArg {
    protected final CommandArgType type
    protected final boolean required
    protected final String displayName
    protected String[] possibilities = null

    CommandArg(CommandArgType type, String displayName, boolean required = true) {
        this.type = type
        this.displayName = displayName
        this.required = required
    }

    CommandArg(CommandArgType type, String displayName, String[] possibilities, boolean required = false) {
        this(type, displayName, required)
        this.possibilities = possibilities
    }

    CommandArgType getType(){
        type
    }

    @Override
    String toString() {
        return (required ? "<" : "[") +
                type.name() + " " +
                displayName +
                (possibilities == null ? "" : ("{" + possibilities.join(", ") + "}")) +
                (required ? ">" : "]")
    }
}
