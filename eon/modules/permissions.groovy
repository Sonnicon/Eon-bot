import com.mongodb.client.model.Filters
import net.dv8tion.jda.api.entities.Message
import org.bson.Document
import org.bson.types.ObjectId
import sonnicon.eonbot.command.Commands
import sonnicon.eonbot.core.Database
import sonnicon.eonbot.core.Modules
import sonnicon.eonbot.type.ExecutorFunc

class permissions extends Modules.ModuleBase {

    void load() {}

    @ExecutorFunc("perms-get")
    boolean permsGet(Map<String, ?> data, Message message) {
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
                doc = Database.getUser(entity as long, guild)
                break

            case ("group"):
                doc = Database.getGroup(entity as String)
                break

            case ("role"):
                doc = Database.getRole(entity as long)
                break

            default:
                return false
        }

        if (doc && doc.containsKey("permissions") && (doc.get("permissions") as Map).containsKey(target)) {
            message.reply((doc.get("permissions") as Map).get(target).toString()).queue()
            return true
        }

        message.reply("Not found.").queue()
        false
    }

    @ExecutorFunc("perms-set")
    boolean permsSet(Map<String, ?> data, Message message) {
        if (!Commands.checkPermissions(message, data.get("target") as String)) {
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
                Database.updateUser(entity as long, guild, doc)
                break

            case ("group"):
                Database.updateGroup(entity as String, doc)
                break

            case ("role"):
                Database.updateRole(entity as long, doc)
                break

            default:
                return false
        }

        true

    }

    @ExecutorFunc("perms-drop")
    boolean permsDrop(Map<String, ?> data, Message message) {
        if (!Commands.checkPermissions(message, data.get("target") as String)) {
            if (message) {
                message.reply("Cannot drop permissions you do not have.").queue()
            }
            return false
        }
        var entity = data.get("entity")
        Document doc = new Document("\$unset",
                new Document().append("permissions.${data.get("target")}", "")
        )
        long guild = 0

        switch (data.get("entityType")) {
            case ("user"):
                if (message) {
                    message.reply("`user` entity is only available in guilds. Please use `globaluser`.").queue()
                    return false
                }
                guild = message.guild.idLong

            case ("globaluser"):
                Database.updateUser(entity as long, guild, doc)
                break

            case ("group"):
                Database.updateGroup(entity as String, doc)
                break

            case ("role"):
                Database.replaceRole(doc)
                break

            default:
                return false
        }
        true
    }

    @ExecutorFunc("group-create")
    boolean groupCreate(Map<String, ?> data, Message message) {
        Database.createGroup(data.get("name") as String)
        true
    }

    @ExecutorFunc("group-delete")
    boolean groupDelete(Map<String, ?> data, Message message) {
        Database.cGroups.deleteOne(Filters.eq("name", data.get("name"))).getDeletedCount() > 0
        true
    }

    @ExecutorFunc("group-add")
    boolean groupAdd(Map<String, ?> data, Message message) {
        Document docGroup = Database.getGroup(data.get("name") as String)
        if (!docGroup) return false
        Document docUser = Database.getUser(data.get("entity") as long)
        Database.updateGroup(data.get("target") as String,
                new Document("\$push",
                        new Document().append("users", docUser.get("_id"))
                )
        )
        Database.updateUser(data.get("entity") as long, 0,
                new Document("\$push",
                        new Document().append("groups", docGroup.get("_id"))
                )
        )
        true
    }

    @ExecutorFunc("group-remove")
    boolean groupRemove(Map<String, ?> data, Message message) {
        Document docGroup = Database.getGroup(data.get("target") as String)
        if (!docGroup) return false
        Document docUser = Database.getUser(data.get("entity") as long)
        Database.updateGroup(data.get("target") as String,
                new Document("\$pull",
                        new Document().append("users", docUser.get("_id"))
                )
        )
        Database.updateUser(data.get("entity") as long, 0,
                new Document("\$pull",
                        new Document().append("groups", docGroup.get("_id"))
                )
        )
        true
    }

    @ExecutorFunc("group-get")
    boolean groupGet(Map<String, ?> data, Message message) {
        var entity = data.get("entity")
        Document doc

        switch (data.get("entityType")) {
            case ("user"):
                doc = Database.getUser(entity as long)
                List groups = doc.get("groups").collect {
                    Database.getGroupById(it as ObjectId).get("name")
                }
                message.reply("Found `${groups.size()}` groups: ```${groups.join(", ")}```").queue()
                break

            case ("group"):
                doc = Database.getGroup(entity as String)
                List users = doc.get("users").collect {
                    Database.getUserById(it as ObjectId).get("user")
                }
                message.reply("Found `${users.size()}` users: ```${users.join(", ")} ```").queue()
                break

            default:
                return false
        }
        true
    }

    void unload() {}
}