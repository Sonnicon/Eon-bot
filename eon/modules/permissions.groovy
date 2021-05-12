import org.bson.Document
import sonnicon.eonbot.core.Database
import sonnicon.eonbot.core.Modules

class ModulePermissions extends Modules.ModuleBase {

    void load() {}

    static Map<String, Closure> getExecutorMap() {
        ["get"   : { data, message ->
            long entity = data.get("entity")
            String target = data.get("target")
            long guild = message.isFromGuild() ? message.guild.idLong : 0
            Document doc

            switch (data.get("entityType")) {
                case ("user"):
                    if (message && guild == 0) {
                        message.reply("`user` entity is only available in guilds. Please use `globaluser`.").queue()
                        return
                    }
                case ("globaluser"):
                    doc = Database.getUser(entity, guild)
                    break
                case ("group"):
                    doc = Database.getGroup(entity)
                    break
                case ("role"):
                    doc = Database.getRole(entity)
                    break
            }

            String response
            if (doc && doc.containsKey("permissions") && doc.get("permissions").containsKey(target)) {
                response = doc.get("permissions").get(target).toString()
            } else {
                response = "Not found."
            }
            if (message) message.reply(response).queue()
            else println(response)

        }, "set" : { data, message ->
            long entity = data.get("entity")
            String target = data.get("target")
            Document doc

            switch (data.get("entityType")) {
                case ("user"):
                    if (message && !message.isFromGuild()) {
                        message.reply("`user` entity is only available in guilds. Please use `globaluser`.").queue()
                        return
                    }
                    doc = Database.getUser(entity, message.guild.idLong)
                    doc.get("permissions").put(target, data.get("value"))
                    Database.updateUser(doc)
                case ("globaluser"):
                    doc = Database.getUser(entity)
                    doc.get("permissions").put(target, data.get("value"))
                    Database.updateUser(doc)
                    break
                case ("group"):
                    doc = Database.getGroup(entity)
                    doc.get("permissions").put(target, data.get("value"))
                    Database.updateGroup(doc)
                    break
                case ("role"):
                    doc = Database.getRole(entity)
                    doc.get("permissions").put(target, data.get("value"))
                    Database.updateRole(doc)
                    break
            }
        }, "drop": { data, message ->
            long entity = data.get("entity")
            String target = data.get("target")
            Document doc

            switch (data.get("entityType")) {
                case ("user"):
                    if (message && !message.isFromGuild()) {
                        message.reply("`user` entity is only available in guilds. Please use `globaluser`.").queue()
                        return
                    }
                    doc = Database.getUser(entity, message.guild.idLong)
                    doc.get("permissions").remove(target)
                    Database.updateUser(doc)
                    break
                case ("globaluser"):
                    doc = Database.getUser(entity)
                    doc.get("permissions").remove(target)
                    Database.updateUser(doc)
                    break
                case ("group"):
                    doc = Database.getGroup(entity)
                    doc.get("permissions").remove(target)
                    Database.updateGroup(doc)
                    break
                case ("role"):
                    doc = Database.getRole(entity)
                    doc.get("permissions").remove(target)
                    Database.updateRole(doc)
                    break
            }
        }]
    }

    void unload() {}
}