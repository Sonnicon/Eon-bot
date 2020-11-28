import sonnicon.eonbot.util.command.CommandArgType

static void main(args) {
    new CommandArgType<Integer>("Integer"){
        @Override
        Integer convert(String input) {
            return Integer.parseInt(input)
        }
    }

    new CommandArgType<String>("String")
}