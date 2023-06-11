package sonnicon.eonbot.core

/**
 * General bot configuration data.
 */
class Config {
    /**
     * Token to authenticate with discord bot user.
     */
    String token = "Token goes here"
    /**
     * MongoDB connection string.
     */
    String mongodbAddress = "mongodb://localhost:27017"
    /**
     * Name of database collection used by bot.
     */
    String mongodbName = "discord_eonbot"

    /**
     * Parse config, create a new one if no config exists.
     * @return Configuration object
     */
    static Config getConfig() {
        File file = FileIO.FileType.config.file
        if (file.exists()) {
            // Parse config if it exists
            FileIO.yamlSlurper.parse(file) as Config
        } else {
            // Write a new config if it doesn't
            FileIO.yamlBuilder.call(new Config())
            file.parentFile.mkdirs()
            Writer writer = file.newWriter()
            FileIO.yamlBuilder.writeTo(writer)
            writer.close()
            new Config()
        }
    }

    /**
     * Getter for discord auth token.
     * @return Token string
     */
    String getToken() {
        if (token.isEmpty())
            throw new RuntimeException("Missing token. Please put token into $FileIO.FileType.config.file.path")
        token
    }
}
