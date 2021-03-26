package sonnicon.eonbot.core

import groovy.yaml.YamlBuilder
import groovy.yaml.YamlSlurper

class FileIO {
    static YamlSlurper yamlSlurper = new YamlSlurper()
    static YamlBuilder yamlBuilder = new YamlBuilder()

    enum FileType {
        root(new File("eon/")),
        config(new File(root.file, "config.yaml")),
        modules(new File(root.file, "modules/"))

        public File file

        FileType(File file) {
            this.file = file
        }
    }
}
