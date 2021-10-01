package sonnicon.eonbot.core

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.ReplaceOptions
import org.bson.Document
import org.bson.types.ObjectId
import sonnicon.eonbot.Eonbot

class Database {
    static MongoClient client
    static MongoDatabase database

    static MongoCollection<Document> cUsers, cGroups, cRoles

    static final ReplaceOptions OPTIONS_UPSERT = new ReplaceOptions(["upsert": true])

    static {
        client = MongoClients.create(Eonbot.config.mongodbAddress)
        database = client.getDatabase(Eonbot.config.mongodbName)

        cUsers = database.getCollection("users")
        cGroups = database.getCollection("groups")
        cRoles = database.getCollection("roles")

        cUsers.createIndex(Indexes.compoundIndex(Indexes.ascending("user"), Indexes.ascending("guild")))
        cGroups.createIndex(Indexes.ascending("name"))
        cRoles.createIndex(Indexes.ascending("role"))

        createGroup("everyone")
    }

    // User

    static Document getUser(long user, long guild = 0) {
        Document d = cUsers.find(Filters.and(Filters.eq("user", user), Filters.eq("guild", guild))).first()
        if (!d) {
            d = new Document(["user": user, "guild": guild, "permissions": [:], "groups": []])
            replaceUser(d)
        }
        d
    }

    static Document getUserById(ObjectId id) {
        Database.cUsers.find(Filters.eq("_id", id)).first()
    }

    static void updateUser(long user, long guild, Document data) {
        cUsers.updateOne(Filters.and(Filters.eq("user", user), Filters.eq("guild", guild)), data)
    }

    static void replaceUser(Document data) {
        cUsers.replaceOne(Filters.and(Filters.eq("user", data.get("user")), Filters.eq("guild", data.get("guild"))), data, OPTIONS_UPSERT)
    }

    // Group

    static Document getGroup(String name) {
        Document d = cGroups.find(Filters.eq("name", name)).first()

        d
    }

    static void createGroup(String name) {
        if (!getGroup(name)) {
            replaceGroup(new Document(["name": name, "permissions": [:], "users": []]))
        }
    }

    static Document getGroupById(ObjectId id) {
        Database.cGroups.find(Filters.eq("_id", id)).first()
    }


    static void updateGroup(String name, Document data) {
        cGroups.updateOne(Filters.eq("name", name), data)
    }

    static void replaceGroup(Document data) {
        cGroups.replaceOne(Filters.eq("name", data.get("name")), data, OPTIONS_UPSERT)
    }

    // Role

    static Document getRole(long role) {
        Document d = cRoles.find(Filters.eq("role", role)).first()
        if (!d) {
            d = new Document(["role": role, "permissions": [:]])
            replaceRole(d)
        }
        d
    }

    static void updateRole(long role, Document data) {
        cGroups.updateOne(Filters.eq("role", role), data)
    }

    static void replaceRole(Document data) {
        cRoles.replaceOne(Filters.eq("role", data.get("role")), data, OPTIONS_UPSERT)
    }
}
