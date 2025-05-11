package com.group7.clubber_backend.Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.group7.clubber_backend.Managers.base.Manager;
import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.types.Comment.Comment; // Assuming Comment.java is corrected
import com.group7.lib.utilities.Conversion.DocumentConverter; // Assuming DocumentConverter will be updated
import com.group7.lib.utilities.Database.Database;
import com.group7.lib.utilities.Database.DatabaseCollection; // Assuming COMMENT will be added to DatabaseCollection
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;

public class CommentManager extends Manager<Comment> {

    private static final CommentManager instance = new CommentManager();
    private final Database database;
    private final Logger logger;
    private static final DatabaseCollection COLLECTION = DatabaseCollection.COMMENT; // Placeholder

    private CommentManager() {
        super();
        this.database = Database.getInstance();
        this.logger = new Logger("Managers/Comment");
    }

    public static CommentManager getInstance() {
        return instance;
    }

    @Override
    public Id create(Comment comment) {
        if (comment == null) {
            logger.log("Attempted to create a null comment", LogLevel.WARNING);
            return null;
        }
        if (comment.id() != null) {
            logger.log("Comment object already has an ID (" + comment.id().toString() + ") before creation.", LogLevel.WARNING);
        }
        // Assuming DocumentConverter.commentToDocument(comment) exists
        Document doc = DocumentConverter.commentToDocument(comment);
        if (doc == null) {
             logger.log("Failed to convert comment to document for creation.", LogLevel.ERROR);
             return null;
         }
        String newId = database.insert(COLLECTION, doc);
        if (newId != null) {
             logger.log("Created comment with ID: " + newId, LogLevel.INFO);
             return new CommentId(newId);
        } else {
            logger.log("Failed to create comment", LogLevel.ERROR);
            return null;
        }
    }

    @Override
    public void update(Comment comment) {
        if (comment == null || comment.id() == null) {
            logger.log("Attempted to update a comment with null object or null ID", LogLevel.WARNING);
            return;
        }
        // Assuming DocumentConverter.commentToDocument(comment) exists
        Document doc = DocumentConverter.commentToDocument(comment);
         if (doc == null) {
             logger.log("Failed to convert comment to document for update.", LogLevel.ERROR);
             return;
         }
        doc.remove("_id");

        boolean success = database.update(COLLECTION, comment.id().toString(), doc);
        if (success) {
            logger.log("Updated comment with ID: " + comment.id().toString(), LogLevel.INFO);
        } else {
            logger.log("Failed to update comment with ID: " + comment.id().toString() + ". It might not exist.", LogLevel.WARNING);
        }
    }

    @Override
    public void delete(Id id) {
        if (id == null) {
            logger.log("Attempted to delete a comment with null ID", LogLevel.WARNING);
            return;
        }
        String commentId = id.toString();
        boolean success = database.delete(COLLECTION, commentId);
        if (success) {
            logger.log("Deleted comment with ID: " + commentId, LogLevel.INFO);
        } else {
            logger.log("Failed to delete comment with ID: " + commentId + ". It might not exist.", LogLevel.WARNING);
        }
    }

    @Override
    public Comment get(Id id) {
        if (id == null) {
            logger.log("Attempted to get a comment with null ID", LogLevel.WARNING);
            return null;
        }
        String commentId = id.toString();
        Document doc = database.get(COLLECTION, commentId);
        if (doc != null) {
            logger.log("Retrieved comment with ID: " + commentId, LogLevel.DEBUG);
            // Assuming DocumentConverter.documentToComment(doc) exists
            return DocumentConverter.documentToComment(doc);
        } else {
            logger.log("Comment not found with ID: " + commentId, LogLevel.INFO);
            return null;
        }
    }

    @Override
    public List<Comment> getAll() {
        List<Document> docs = database.list(COLLECTION, new Document());
        logger.log("Retrieved " + docs.size() + " comments", LogLevel.DEBUG);
        return docs.stream()
                // Assuming DocumentConverter.documentToComment(doc) exists
                .map(DocumentConverter::documentToComment)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> list(Id[] ids) {
        if (ids == null || ids.length == 0) {
            return new ArrayList<>();
        }

        List<ObjectId> objectIds = Arrays.stream(ids)
                .filter(java.util.Objects::nonNull)
                .filter(id -> id instanceof CommentId) // Check for CommentId
                .map(id -> {
                    try {
                        return new ObjectId(id.toString());
                    } catch (IllegalArgumentException e) {
                         logger.log("Invalid ObjectId format in list: " + id.toString(), LogLevel.WARNING);
                         return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        if (objectIds.isEmpty()) {
             logger.log("No valid CommentIds provided in list query.", LogLevel.DEBUG);
            return new ArrayList<>();
        }

        Document query = new Document("_id", new Document("$in", objectIds));

        List<Document> docs = database.list(COLLECTION, query);
        logger.log("Retrieved " + docs.size() + " comments from list query", LogLevel.DEBUG);
        return docs.stream()
                // Assuming DocumentConverter.documentToComment(doc) exists
                .map(DocumentConverter::documentToComment)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Basic search by a single field:value string. 
    // Consider adding more sophisticated search or filtering if needed.
    @Override
    public List<Comment> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            logger.log("Search query is null or empty for comments.", LogLevel.WARNING);
            return new ArrayList<>();
        }

        String[] parts = query.split(":", 2);
        if (parts.length != 2) {
            logger.log("Invalid search query format for comments. Expected 'field:value', got: " + query, LogLevel.WARNING);
            return new ArrayList<>();
        }

        String field = parts[0].trim();
        String value = parts[1].trim();

        if (field.isEmpty() || value.isEmpty()) {
            logger.log("Search field or value is empty in comment query: " + query, LogLevel.WARNING);
            return new ArrayList<>();
        }

        logger.log("Searching for comments with " + field + " = " + value, LogLevel.INFO);

        // For comments, fields like 'reviewId' or 'userId' might be common search targets.
        // These are likely stored as ObjectIds or strings in the database.
        // No special conversion is shown here, assuming direct string match or ObjectId string match.
        // If searching by `userId` or `reviewId` which are `Id` types, ensure your DB stores them as strings
        // or convert `value` to `ObjectId` if necessary (similar to the `list` method).
        Document searchQuery = new Document(field, value);
        List<Document> docs = database.list(COLLECTION, searchQuery);

        if (docs == null || docs.isEmpty()) {
            logger.log("No comments found for query: " + query, LogLevel.INFO);
            return new ArrayList<>();
        }

        return docs.stream()
                // Assuming DocumentConverter.documentToComment(doc) exists
                .map(DocumentConverter::documentToComment)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
} 