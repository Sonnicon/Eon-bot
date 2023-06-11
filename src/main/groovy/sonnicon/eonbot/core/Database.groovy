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

/**
 * Database connection and initialization class.
 */
class Database {
    static MongoClient client
    static MongoDatabase database

    /**
     * MongoDB collections for users, groups, and roles
     */
    static MongoCollection<Document> cUsers, cGroups, cRoles

    static final ReplaceOptions OPTIONS_UPSERT = new ReplaceOptions(["upsert": true])

    static {
        // Connect to MongoDB
        client = MongoClients.create(Eonbot.config.mongodbAddress)
        database = client.getDatabase(Eonbot.config.mongodbName)

        // Get all relevant collections
        cUsers = database.getCollection("users")
        cGroups = database.getCollection("groups")
        cRoles = database.getCollection("roles")

        // Create all relevant indexes
        cUsers.createIndex(Indexes.compoundIndex(Indexes.ascending("user"), Indexes.ascending("guild")))
        cGroups.createIndex(Indexes.ascending("name"))
        cRoles.createIndex(Indexes.ascending("role"))

        // Initialize global everyone group
        createGroup("everyone")
    }

    // User

    /**
     * Get all data on a user from database. Create user if missing.
     * @param user User ID to fetch
     * @param guild Guild for which to fetch (0=global)
     * @return Document of user data
     */
    static Document getUser(long user, long guild = 0) {
        Document d = cUsers.find(Filters.and(Filters.eq("user", user), Filters.eq("guild", guild))).first()
        // Create if not found
        if (!d) {
            d = new Document(["user": user, "guild": guild, "permissions": [:], "groups": []])
            replaceUser(d)
        }
        d
    }

    /**
     * Get user by MongoDB ID.
     * @param MongoDB object ID.
     * @return User document
     */
    static Document getUserById(ObjectId id) {
        Database.cUsers.find(Filters.eq("_id", id)).first()
    }

    /**
     * Update a target user in a guild in the database with new data.
     * @param user User ID of the target user
     * @param guild Guild for which data to get
     * @param data Document with fields to be updated
     */
    static void updateUser(long user, long guild, Document data) {
        cUsers.updateOne(Filters.and(Filters.eq("user", user), Filters.eq("guild", guild)), data)
    }

    /**
     * Replace a user data in the database.
     * @param data Document of the user, with guild and user fields, and other fields to upsert.
     */
    static void replaceUser(Document data) {
        cUsers.replaceOne(Filters.and(Filters.eq("user", data.get("user")), Filters.eq("guild", data.get("guild"))), data, OPTIONS_UPSERT)
    }

    // Group

    /**
     * Get a group by name from the database.
     * @param name Name of the group
     * @return Group document
     */
    static Document getGroup(String name) {
        cGroups.find(Filters.eq("name", name)).first()
    }

    /**
     * Create a group in the database if it doesn't already exist.
     * @param name Name of group to create
     */
    static void createGroup(String name) {
        if (!getGroup(name)) {
            replaceGroup(new Document(["name": name, "permissions": [:], "users": []]))
        }
    }

    /**
     * Get group by mongodb ID.
     * @param id MongoDB ID of group
     * @return Group document
     */
    static Document getGroupById(ObjectId id) {
        Database.cGroups.find(Filters.eq("_id", id)).first()
    }

    /**
     * Update a target group in the database with new data.
     * @param name Name of the target group
     * @param data Document with fields to be updated
     */
    static void updateGroup(String name, Document data) {
        cGroups.updateOne(Filters.eq("name", name), data)
    }

    /**
     * Replace a group data in the database.
     * @param data Document of the group, with name field, and other fields to upsert.
     */
    static void replaceGroup(Document data) {
        cGroups.replaceOne(Filters.eq("name", data.get("name")), data, OPTIONS_UPSERT)
    }

    // Role
    /**
     * Get all data on a role from database. Create database role if missing.
     * @param role Role ID to get
     * @return Document of role data
     */
    static Document getRole(long role) {
        Document d = cRoles.find(Filters.eq("role", role)).first()
        if (!d) {
            d = new Document(["role": role, "permissions": [:]])
            replaceRole(d)
        }
        d
    }

    /**
     * Update a target role in the database with new data.
     * @param role Role ID of the target role
     * @param data Document with fields to be updated
     */
    static void updateRole(long role, Document data) {
        cGroups.updateOne(Filters.eq("role", role), data)
    }

    /**
     * Replace a role data in the database.
     * @param data Document of the role, with role field, and other fields to upsert.
     */
    static void replaceRole(Document data) {
        cRoles.replaceOne(Filters.eq("role", data.get("role")), data, OPTIONS_UPSERT)
    }
}
