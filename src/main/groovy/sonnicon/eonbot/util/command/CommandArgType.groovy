package sonnicon.eonbot.util.command

class CommandArgType<T> {
    protected final String name

    protected final static HashMap<String, CommandArgType> types = [:]

    CommandArgType(String name) {
        this.name = name
        types.put(name, this)
    }

    T convert(String input) {
        return (T) input
    }

    String name() {
        return name
    }

    static CommandArgType getType(String name) {
        return types.get(name)
    }

}