package sonnicon.eonbot.util.command

class CommandArgType<T> {
    protected final String name

    protected final static HashMap<String, CommandArgType> types = [:]

    public CommandArgType(String name) {
        this.name = name
        types.put(name, this)
    }

    public T convert(String input) {
        return (T) input
    }

    public String name() {
        return name
    }

    public static CommandArgType getType(String name) {
        return types.get(name)
    }

}