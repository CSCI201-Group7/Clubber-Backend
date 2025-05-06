package com.group7.clubber_backend.Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.group7.clubber_backend.Managers.base.Manager;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.types.User.User;
import com.group7.lib.utilities.Conversion.DocumentConverter;
import com.group7.lib.utilities.Database.Database;
import com.group7.lib.utilities.Database.DatabaseCollection;
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;

public class UserManager extends Manager<User> {

    private static final UserManager instance = new UserManager();
    private final Database database;
    private final Logger logger;
    private static final DatabaseCollection COLLECTION = DatabaseCollection.USER;

    private UserManager() {
        super();
        this.database = Database.getInstance();
        this.logger = new Logger("Managers/User");
    }

    public static UserManager getInstance() {
        return instance;
    }

    @Override
    public Id create(User user) {
        Document doc = DocumentConverter.userToDocument(user);
        if (doc == null) {
            logger.log("Failed to convert user to document for creation.", LogLevel.ERROR);
            return null;
        }

        String newId = database.insert(COLLECTION, doc);
        if (newId != null) {
            logger.log("Created user with ID: " + newId, LogLevel.INFO);
            return new UserId(newId);
        } else {
            logger.log("Failed to create user: " + user.getUsername(), LogLevel.ERROR);
            return null;
        }
    }

    @Override
    public void update(User user) {
        if (user == null || user.getId() == null) {
            logger.log("Attempted to update user with null object or null ID", LogLevel.WARNING);
            return;
        }
        Document doc = DocumentConverter.userToDocument(user);
        if (doc == null) {
            logger.log("Failed to convert user to document for update.", LogLevel.ERROR);
            return;
        }
        doc.remove("_id");

        boolean success = database.update(COLLECTION, user.getId().toString(), doc);
        if (success) {
            logger.log("Updated user with ID: " + user.getId().toString(), LogLevel.INFO);
        } else {
            logger.log("Failed to update user with ID: " + user.getId().toString(), LogLevel.WARNING);
        }
    }

    @Override
    public void delete(Id id) {
        if (id == null) {
            logger.log("Attempted to delete user with null ID", LogLevel.WARNING);
            return;
        }
        boolean success = database.delete(COLLECTION, id.toString());
        if (success) {
            logger.log("Deleted user with ID: " + id.toString(), LogLevel.INFO);
        } else {
            logger.log("Failed to delete user with ID: " + id.toString() + " (might not exist)", LogLevel.WARNING);
        }
    }

    @Override
    public User get(Id id) {
        if (id == null) {
            logger.log("Attempted to get user with null ID", LogLevel.WARNING);
            return null;
        }
        logger.log("Getting user with ID: " + id.toString(), LogLevel.INFO);
        Document doc = database.get(COLLECTION, id.toString());
        if (doc == null) {
            logger.log("User not found with ID: " + id.toString(), LogLevel.WARNING);
            return null;
        }
        return DocumentConverter.documentToUser(doc);
    }

    @Override
    public List<User> getAll() {
        logger.log("Getting all users", LogLevel.INFO);
        List<Document> docs = database.list(COLLECTION, new Document());
        return docs.stream()
                .map(DocumentConverter::documentToUser)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> list(Id[] ids) {
        if (ids == null || ids.length == 0) {
            return new ArrayList<>();
        }
        logger.log("Getting users with specific IDs", LogLevel.INFO);
        List<ObjectId> objectIds = Arrays.stream(ids)
                .filter(java.util.Objects::nonNull)
                .map(id -> new ObjectId(id.toString()))
                .collect(Collectors.toList());
        if (objectIds.isEmpty()) {
            return new ArrayList<>();
        }
        Document query = new Document("_id", new Document("$in", objectIds));
        List<Document> docs = database.list(COLLECTION, query);
        return docs.stream()
                .map(DocumentConverter::documentToUser)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            logger.log("Search query is null or empty.", LogLevel.WARNING);
            return new ArrayList<>();
        }

        String[] parts = query.split(":", 2);
        if (parts.length != 2) {
            logger.log("Invalid search query format. Expected 'field:value', got: " + query, LogLevel.WARNING);
            return new ArrayList<>();
        }

        String field = parts[0].trim();
        String value = parts[1].trim();

        if (field.isEmpty() || value.isEmpty()) {
            logger.log("Search field or value is empty in query: " + query, LogLevel.WARNING);
            return new ArrayList<>();
        }

        logger.log("Searching for users with " + field + " = " + value, LogLevel.INFO);

        Document searchQuery;
        if (field.equalsIgnoreCase("username") || field.equalsIgnoreCase("email")) {
            // Case-insensitive search for username and email
            searchQuery = new Document(field, Pattern.compile("^" + Pattern.quote(value) + "$", Pattern.CASE_INSENSITIVE));
        } else {
            // Case-sensitive search for other fields
            searchQuery = new Document(field, value);
        }
        
        List<Document> docs = database.list(COLLECTION, searchQuery);

        if (docs == null || docs.isEmpty()) {
            logger.log("No users found for query: " + query, LogLevel.INFO);
            return new ArrayList<>();
        }

        return docs.stream()
                .map(DocumentConverter::documentToUser)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}
