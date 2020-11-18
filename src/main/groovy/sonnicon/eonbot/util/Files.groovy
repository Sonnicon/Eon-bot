package sonnicon.eonbot.util

import org.yaml.snakeyaml.Yaml
import sonnicon.eonbot.Eonbot
import sonnicon.eonbot.core.Config

import java.util.regex.Pattern

class Files {
    static final Yaml yaml = new Yaml()

    static final File main = new File("eon/")
    static final File modules = new File(main, "modules/")

    static init() {
        File configFile = new File(main, "config.yaml")
        if (configFile.exists() && configFile.length() > 0) {
            def reader = new FileReader(configFile)
            Eonbot.config = yaml.load(reader) as Config
            reader.close()
        } else {
            configFile.getParentFile().mkdirs()
            configFile.createNewFile()
            Eonbot.config = new Config()
            def writer = new FileWriter(configFile)
            yaml.dump(Eonbot.config.getMap(), writer)
            writer.close()

            System.err.println("Please save token into ${configFile.getAbsolutePath()}")
            System.exit(1)
        }

        modules.mkdirs()
    }

    static File fileModule(String module) {
        new File(modules, module + ".groovy")
    }

    static boolean verify(String name) {
        return Pattern.matches("^[a-zA-Z0-9\\s]+\$", name)
    }
}
