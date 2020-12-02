import messagesutil
import net.dv8tion.jda.api.entities.Message
import sonnicon.eonbot.util.Files
import sonnicon.eonbot.util.command.Command
import sonnicon.eonbot.util.command.CommandArg
import sonnicon.eonbot.util.command.CommandArgType

//shoddy markov chains
static void main(arg) {
    new File(Files.modules, "markov").mkdirs()

    HashMap<String, HashMap<String, Integer>> datas
    String name
    Boolean loaded = false

    new Command("markov unload", [] as CommandArg[], { event ->
        loaded = false
        name = null
        datas = null
        messagesutil.reply(event, "Unloaded current chain (if any)")
    })

    new Command("markov loaded", [] as CommandArg[], { event ->
        if (loaded) {
            messagesutil.reply(event, "Chain `" + name + "` is currently loaded")
        } else {
            messagesutil.reply(event, "No chain is currently loaded")
        }
    })

    new Command("markov load", [new CommandArg(CommandArgType.getType("String"), "Chain")] as CommandArg[],
            { event, arg1 ->
                if (!Files.verify(arg1)) {
                    messagesutil.reply(event, "Illegal value")
                    return
                }
                File target = getTarget(arg1)
                if (target.exists()) {
                    FileReader reader = new FileReader(target)
                    datas = Files.yaml.load(reader)
                    reader.close()
                    messagesutil.reply(event, "Loaded datas from `" + target.getName() + "`")
                } else {
                    datas = new HashMap<>()
                    datas.put("@start", new HashMap<String, Integer>())
                    messagesutil.reply(event, "Starting new chain `" + arg1 + "`")
                }
                loaded = true
                name = arg1
            })

    new Command("markov save", [] as CommandArg[], { event ->
        if (loaded) {
            File target = getTarget(name)
            FileWriter writer = new FileWriter(target)
            Files.yaml.dump(datas, writer)
            writer.close()
            messagesutil.reply(event, "Saved datas to `" + target.getName() + "`")
        } else {
            messagesutil.reply(event, "Nothing is loaded")
        }
    })

    new Command("markov add", [new CommandArg(CommandArgType.getType("String"), "Text", true)] as CommandArg[],
            { event, arg1 = "" ->

                if (!loaded) {
                    messagesutil.reply(event, "Nothing is loaded")
                    return
                }

                InputStream stream

                // initialize stream
                if (event.message.attachments.size() > 0) {
                    Message.Attachment a = event.message.attachments.get(0)
                    if (!a.image && !a.video && a.size <= 5000000) {
                        stream = a.retrieveInputStream().get()
                    } else {
                        messagesutil.reply(event, "Bad attachment")
                        return
                    }
                } else {
                    stream = new ByteArrayInputStream(arg1.getBytes("UTF-8"))
                }

                HashMap m = datas.get("@start")
                while (true) {
                    ByteArrayOutputStream result = new ByteArrayOutputStream()
                    boolean end = false

                    // read word
                    int r
                    while (true) {
                        r = stream.read()
                        if (r as char == " " as char) {
                            break
                        } else if (".;?!".indexOf(r) != -1 || r == -1) {
                            // new sentences
                            end = true
                            break
                        } else if (",:'\"()[]".indexOf(r) != -1) {
                            // ignored characters
                            continue
                        }
                        result.write(r)
                    }

                    // add to maps
                    String w = result.toString("UTF-8").toLowerCase()
                    if (w.isEmpty() && r != -1) continue

                    m.put(w, m.getOrDefault(w, 0) + 1)
                    if (datas.containsKey(w)) {
                        m = datas.get(w)
                    } else {
                        m = new HashMap<String, Integer>()
                        datas.put(w, m)
                    }

                    if (end) {
                        m.put("@end", m.getOrDefault("@end", 0) + 1)
                        if (r == -1) {
                            break
                        }
                        m = datas.get("@start")
                    }
                }
                stream.close()
                messagesutil.reply(event, "Added sentence(s) to markov chain `" + name + "`")
            }, true)

    Random random = new Random()

    new Command("markov generate", [] as CommandArg[], { event ->
        if (!loaded) {
            messagesutil.reply(event, "Nothing is loaded")
            return
        }

        StringJoiner joiner = new StringJoiner(" ")
        String next = "@start"

        while (true) {
            HashMap<String, Integer> map = datas.get(next)
            int total = map.values().sum()

            int index = random.nextInt(total) + 1
            next = map.entrySet().find {
                index -= it.value
                return index <= 0
            }.key

            if (next.equals("@end") || joiner.length() > 350) {
                break
            }
            joiner.add(next)
        }

        messagesutil.reply(event, joiner.toString())
    })
}

static File getTarget(String name) {
    new File(Files.modules, "markov/" + name + ".yaml")
}