import sonnicon.eonbot.util.Commands
import sonnicon.eonbot.util.Files

import static messagesutil

//shoddy markov chains
static void main(arg) {
    new File(Files.modules, "markov").mkdirs()

    HashMap<String, HashMap<String, Integer>> datas
    String name
    Boolean loaded = false

    Commands commands = new Commands()

    commands.newCommand("markov load", { event, args ->
        if (args.size() > 0) {
            name = args[0]
            if(!Files.verify(name)){
                messagesutil.reply(event, "Illegal value")
                return
            }
            File target = getTarget(name)
            if(target.exists()){
                FileReader reader = new FileReader(target)
                datas = Files.yaml.load(reader)
                reader.close()
                messagesutil.reply(event, "Loaded datas from `" + target.getName() + "`")
            }else{
                datas = new HashMap<>()
                datas.put("@start", new HashMap<String, Integer>())
                messagesutil.reply(event, "Starting new chain `" + name + "`")
            }
            loaded = true
        }
    })

    commands.newCommand("markov save", { event, args ->
        if(loaded) {
            File target = getTarget(name)
            FileWriter writer = new FileWriter(target)
            Files.yaml.dump(datas, writer)
            writer.close()
            messagesutil.reply(event, "Saved datas to `" + target.getName() + "`")
        }else{
            messagesutil.reply(event, "Nothing is loaded")
        }
    })

    commands.newCommand("markov add", { event, args ->
        if(!loaded) {
            messagesutil.reply(event, "Nothing is loaded")
            return
        }
        ArrayList<String> list = new ArrayList()
        for(String s : args){
            list.addAll(Arrays.asList(s.split(" ")))
        }

        HashMap m = datas.get("@start")
        for(int i = 0; i < list.size(); i++){
            String w = list.get(i).toLowerCase().replaceAll("[^a-zA-Z]", "")

            m.put(w, m.getOrDefault(w, 0) + 1)
            if(datas.containsKey(w)){
                m = datas.get(w)
            }else{
                m = new HashMap<String, Integer>()
                datas.put(w, m)
            }

            if(i == list.size() - 1){
                m.put("@end", m.getOrDefault(w, 0) + 1)
            }
        }
        messagesutil.reply(event, "Added sentence to markov chain `" + name + "`")
    })

    Random random = new Random()

    commands.newCommand("markov generate", { event, args ->
        if(!loaded) {
            messagesutil.reply(event, "Nothing is loaded")
            return
        }

        StringJoiner joiner = new StringJoiner(" ")
        String next = "@start"

        while(true){
            HashMap<String, Integer> map = datas.get(next)
            int total = map.values().sum()

            int index = random.nextInt(total) + 1
            next = map.entrySet().find {
                index -= it.value
                return index <= 0
            }.key

            if(next.equals("@end") || joiner.length() > 350){
                break
            }
            joiner.add(next)
        }

        messagesutil.reply(event, joiner.toString())
    })
}

static File getTarget(String name){
    new File(Files.modules, "markov/" + name + ".yaml")
}