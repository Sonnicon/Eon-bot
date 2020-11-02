import sonnicon.eonbot.core.Modules

static void main(arg) {
    String[] modules = ["log", "permissions", "modules", "basiccommands", "markov"]
    modules.each {Modules.loadModule(it)}
}