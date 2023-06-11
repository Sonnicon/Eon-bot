package sonnicon.eonbot.core

import groovy.yaml.YamlBuilder
import groovy.yaml.YamlSlurper

/**
 * Filesystem constants and helper functions.
 */
class FileIO {
    /**
     * Yaml Slurper for parsing YAML
     */
    static YamlSlurper yamlSlurper = new YamlSlurper()
    /**
     * Yaml Builder for writing YAML
     */
    static YamlBuilder yamlBuilder = new YamlBuilder()

    /**
     * Constant file paths for configurations
     */
    enum FileType {
        /**
         * Root configuration directory for everything
         */
        root(new File("eon/")),
        /**
         * Config file for general bot configuration
         */
        config(new File(root.file, "config.yaml")),
        /**
         * Directory containing modules
         */
        modules(new File(root.file, "modules/"))

        public File file

        FileType(File file) {
            this.file = file
        }
    }

    /**
     * Limits filenames to alphanumeric and underscore. Other characters become underscores.
     * @param string String to cleanse
     * @return Cleansed string
     */
    static String sanitizeFilename(String string) {
        string.replace("[^a-zA-Z\\d_]", "_")
    }
}
