package sonnicon.eonbot.core

class Config {
    String token = "Token goes here"

    static Config getConfig() {
        File file = FileIO.FileType.config.file
        if (file.exists()) {
            FileIO.yamlSlurper.parse(file) as Config
        } else {
            FileIO.yamlBuilder.call(new Config())
            file.parentFile.mkdirs()
            Writer writer = file.newWriter()
            FileIO.yamlBuilder.writeTo(writer)
            writer.close()
            new Config()
        }
    }

    String getToken() {
        if (token.isEmpty())
            throw new RuntimeException("Missing token. Please put token into $FileIO.FileType.config.file.path")
        token
    }
}
