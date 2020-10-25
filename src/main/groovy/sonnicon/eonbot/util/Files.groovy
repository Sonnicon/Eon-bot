package sonnicon.eonbot.util

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import sonnicon.eonbot.Eonbot
import sonnicon.eonbot.core.Config

class Files {
    static final Gson gson = new Gson()

    static final File main = new File("eon/")
    static final File modules = new File(main, "modules/")

    static init() {
        File configFile = new File(main, "config.json")
        if (configFile.exists() && configFile.length() > 0) {
            def reader = new JsonReader(new FileReader(configFile))
            Eonbot.config = gson.fromJson(reader, Config)
            reader.close()
        } else {
            configFile.getParentFile().mkdirs()
            configFile.createNewFile()
            Eonbot.config = new Config()
            def writer = new FileWriter(configFile)
            gson.toJson(Eonbot.config, writer)
            writer.close()
            System.err.println("Please save token into ${configFile.getAbsolutePath()}")
            System.exit(1)
        }
        modules.mkdirs()
    }

    static File fileModule(String module) {
        return new File(modules, module + ".groovy")
    }
}
