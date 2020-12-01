import sonnicon.eonbot.util.command.CommandArgType

import java.util.regex.Matcher
import java.util.regex.Pattern

static void main(args) {
    new CommandArgType<Integer>("Integer"){
        @Override
        Integer convert(String input) {
            return Integer.parseInt(input)
        }
    }

    new CommandArgType<Boolean>("Boolean"){
        @Override
        Boolean convert(String input) {
            return Boolean.parseBoolean(input)
        }
    }

    new CommandArgType<String>("String")

    Pattern userRegex = Pattern.compile("(^\\d+\$)|(?<=^<@!)(\\d+)(?=>\$)")
    new CommandArgType<Long>("User"){
        @Override
        Long convert(String input) {
            Matcher m = userRegex.matcher(input)
            if(m.find()){
                return Long.parseLong(m.group())
            }else{
                throw new IllegalArgumentException("Could not parse user")
            }
        }
    }
}