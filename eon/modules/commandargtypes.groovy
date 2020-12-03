import sonnicon.eonbot.util.command.CommandArgType
import sonnicon.eonbot.util.command.Command
import sonnicon.eonbot.util.command.Commands

import java.util.regex.Matcher
import java.util.regex.Pattern

static void main(args) {
    new CommandArgType<Short>("Short") {
        @Override
        Short convert(String input) {
            return Short.parseShort(input)
        }
    }

    new CommandArgType<Integer>("Integer") {
        @Override
        Integer convert(String input) {
            return Integer.parseInt(input)
        }
    }

    new CommandArgType<Long>("Long") {
        @Override
        Long convert(String input) {
            return Long.parseLong(input)
        }
    }

    new CommandArgType<Boolean>("Boolean") {
        @Override
        Boolean convert(String input) {
            return Boolean.parseBoolean(input)
        }
    }

    new CommandArgType<String>("String")

    Pattern userRegex = Pattern.compile("(^\\d+\$)|(?<=^<@!)(\\d+)(?=>\$)")
    new CommandArgType<Long>("User") {
        @Override
        Long convert(String input) {
            Matcher m = userRegex.matcher(input)
            if (m.find()) {
                return Long.parseLong(m.group())
            } else {
                throw new IllegalArgumentException("Could not parse user")
            }
        }
    }

    new CommandArgType<Command>("Command") {
        @Override
        Command convert(String input) {
            Command c = Commands.getCommand(input)
            if (c == null) {
                throw new IllegalArgumentException("Command not found")
            }
            c
        }
    }
}