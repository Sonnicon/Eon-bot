package sonnicon.util

import sonnicon.core.Config

class Files {
    static final File main = new File("eon/")
    static final File modules = new File(main, "modules/")

    static init() {
        def config = new File(main, "token.txt")
        if (config.exists() && config.length() > 0) {
            Config.setToken(config.getText())
        } else {
            config.getParentFile().mkdirs()
            config.createNewFile()
            System.err.println("Please save token into ${config.getAbsolutePath()}")
            System.exit(1)
        }

        modules.mkdirs()
    }

    static File fileModule(String module) {
        return new File(modules, module + ".groovy")
    }
}
