import com.mongodb.client.model.Filters
import org.bson.Document
import sonnicon.eonbot.command.Commands
import sonnicon.eonbot.core.Database
import sonnicon.eonbot.core.Modules

class ModulePermissions extends Modules.ModuleBase {

    void load() {}

    static Map<String, Closure> getExecutorMap() {
        ["perms-get"     : { data, message ->
            var entity = data.get("entity")
            String target = data.get("target")
            long guild = 0
            Document doc

            switch (data.get("entityType")) {
                case ("user"):
                    if (message && !message.isFromGuild()) {
                        message.reply("`user` entity is only available in guilds. Please use `globaluser`.").queue()
                        return false
                    }
                    guild = message.guild.idLong
                case ("globaluser"):
                    doc = Database.getUser(entity, guild)
                    break
                case ("group"):
                    doc = Database.getGroup(entity)
                    break
                case ("role"):
                    doc = Database.getRole(entity)
                    break
                default:
                    return false
            }

            if (doc && doc.containsKey("permissions") && doc.get("permissions").containsKey(target)) {
                message.reply(doc.get("permissions").get(target).toString()).queue()
                return true
            }

            message.reply("Not found.").queue()
            false

        }, "perms-set"   : { data, message ->
            if (!Commands.checkPermissions(message, data.get("target"))) {
                if (message && !message.isFromGuild()) {
                    message.reply("Cannot set permissions you do not have.").queue()
                }
                return false
            }

            var entity = data.get("entity")
            Document doc = new Document("\$set",
                    new Document().append("permissions.${data.get("target")}", data.get("value")))
            long guild = 0

            switch (data.get("entityType")) {
                case ("user"):
                    if (message && !message.isFromGuild()) {
                        message.reply("`user` entity is only available in guilds. Please use `globaluser`.").queue()
                        return false
                    }
                    guild = message.guild.idLong

                case ("globaluser"):
                    Database.updateUser(entity, guild, doc)
                    break

                case ("group"):
                    Database.updateGroup(entity, doc)
                    break

                case ("role"):
                    Database.updateRole(entity, doc)
                    break

                default:
                    return false
            }

            true
        }, "perms-drop"  : { data, message ->
            if (!Commands.checkPermissions(message, data.get("target"))) {
                if (message && !message.isFromGuild()) {
                    message.reply("Cannot drop permissions you do not have.").queue()
                }
                return false
            }
            var entity = data.get("entity")
            Document doc = new Document("\$unset",
                    new Document().append("permissions.${data.get("target")}", ""))
            long guild = 0

            switch (data.get("entityType")) {
                case ("user"):
                    if (message && !message.isFromGuild()) {
                        message.reply("`user` entity is only available in guilds. Please use `globaluser`.").queue()
                        return
                    }
                    guild = message.guild.idLong

                case ("globaluser"):
                    Database.updateUser(entity, guild, doc)
                    break

                case ("group"):
                    Database.updateGroup(entity, doc)
                    break

                case ("role"):
                    Database.replaceRole(doc)
                    break

                default:
                    return false
            }
            true

        }, "group-create": { data, message ->
            Database.createGroup(data.get("name"))
            true

        }, "group-delete": { data, message ->
            Database.cGroups.deleteOne(Filters.eq("name", data.get("name"))).getDeletedCount() > 0

        }, "group-add"   : { data, message ->
            Document docGroup = Database.getGroup(data.get("name"))
            if (!docGroup) return false
            Document docUser = Database.getUser(data.get("entity"))
            Database.updateGroup(data.get("target"),
                    new Document("\$push", new Document().append("users", docUser.get("_id"))))
            Database.updateUser(data.get("entity"), 0,
                    new Document("\$push", new Document().append("groups", docGroup.get("_id"))))
            true

        }, "group-remove": { data, message ->
            Document docGroup = Database.getGroup(data.get("target"))
            if (!docGroup) return false
            Document docUser = Database.getUser(data.get("entity"))
            Database.updateGroup(data.get("target"),
                    new Document("\$pull", new Document().append("users", docUser.get("_id"))))
            Database.updateUser(data.get("entity"), 0,
                    new Document("\$pull", new Document().append("groups", docGroup.get("_id"))))
            true

        }, "group-get"   : { data, message ->
            var entity = data.get("entity")
            Document doc

            switch (data.get("entityType")) {
                case ("user"):
                    doc = Database.getUser(entity)
                    List groups = doc.get("groups").collect { Database.getGroupById(it).get("name") }
                    message.reply("Found `${groups.size()}` groups: ```${groups.join(", ")}```").queue()
                    break

                case ("group"):
                    doc = Database.getGroup(entity)
                    List users = doc.get("users").collect { Database.getUserById(it).get("user") }
                    message.reply("Found `${users.size()}` users: ```${users.join(", ")} ```").queue()
                    break

                default:
                    return false
            }
            true
        }]
    }

    void unload() {}
}