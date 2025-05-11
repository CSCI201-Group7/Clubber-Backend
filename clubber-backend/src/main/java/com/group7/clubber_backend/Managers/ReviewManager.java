package com.group7.clubber_backend.Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.group7.clubber_backend.Managers.base.Manager;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.types.Review.Review; // Assuming Review.java is created
import com.group7.lib.utilities.Conversion.DocumentConverter; // Assuming DocumentConverter will be updated
import com.group7.lib.utilities.Database.Database;
import com.group7.lib.utilities.Database.DatabaseCollection; // Assuming REVIEW will be added to DatabaseCollection
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;

public class ReviewManager extends Manager<Review> {

    private static final ReviewManager instance = new ReviewManager();
    private final Database database;
    private final Logger logger;
    private static final DatabaseCollection COLLECTION = DatabaseCollection.REVIEW; // Placeholder

    private ReviewManager() {
        super();
        this.database = Database.getInstance();
        this.logger = new Logger("Managers/Review");
    }

    public static ReviewManager getInstance() {
        return instance;
    }

    @Override
    public Id create(Review review) {
        if (review == null) {
            logger.log("Attempted to create a null review", LogLevel.WARNING);
            return null;
        }
        if (review.id() != null) {
            logger.log("Review object already has an ID (" + review.id().toString() + ") before creation.", LogLevel.WARNING);
        }
        // Assuming DocumentConverter.reviewToDocument(review) exists
        Document doc = DocumentConverter.reviewToDocument(review);
        if (doc == null) {
             logger.log("Failed to convert review to document for creation.", LogLevel.ERROR);
             return null;
         }
        String newId = database.insert(COLLECTION, doc);
        if (newId != null) {
             logger.log("Created review with ID: " + newId, LogLevel.INFO);
             return new ReviewId(newId);
        } else {
            logger.log("Failed to create review", LogLevel.ERROR); // Potentially add more review details
            return null;
        }
    }

    @Override
    public void update(Review review) {
        if (review == null || review.id() == null) {
            logger.log("Attempted to update a review with null object or null ID", LogLevel.WARNING);
            return;
        }
        // Assuming DocumentConverter.reviewToDocument(review) exists
        Document doc = DocumentConverter.reviewToDocument(review);
         if (doc == null) {
             logger.log("Failed to convert review to document for update.", LogLevel.ERROR);
             return;
         }
        doc.remove("_id");

        boolean success = database.update(COLLECTION, review.id().toString(), doc);
        if (success) {
            logger.log("Updated review with ID: " + review.id().toString(), LogLevel.INFO);
        } else {
            logger.log("Failed to update review with ID: " + review.id().toString() + ". It might not exist.", LogLevel.WARNING);
        }
    }

    @Override
    public void delete(Id id) {
        if (id == null) {
            logger.log("Attempted to delete a review with null ID", LogLevel.WARNING);
            return;
        }
        String reviewId = id.toString();
        boolean success = database.delete(COLLECTION, reviewId);
        if (success) {
            logger.log("Deleted review with ID: " + reviewId, LogLevel.INFO);
        } else {
            logger.log("Failed to delete review with ID: " + reviewId + ". It might not exist.", LogLevel.WARNING);
        }
    }

    @Override
    public Review get(Id id) {
        if (id == null) {
            logger.log("Attempted to get a review with null ID", LogLevel.WARNING);
            return null;
        }
        String reviewId = id.toString();
        Document doc = database.get(COLLECTION, reviewId);
        if (doc != null) {
            logger.log("Retrieved review with ID: " + reviewId, LogLevel.DEBUG);
            // Assuming DocumentConverter.documentToReview(doc) exists
            return DocumentConverter.documentToReview(doc);
        } else {
            logger.log("Review not found with ID: " + reviewId, LogLevel.INFO);
            return null;
        }
    }

    @Override
    public List<Review> getAll() {
        List<Document> docs = database.list(COLLECTION, new Document());
        logger.log("Retrieved " + docs.size() + " reviews", LogLevel.DEBUG);
        return docs.stream()
                // Assuming DocumentConverter.documentToReview(doc) exists
                .map(DocumentConverter::documentToReview)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> list(Id[] ids) {
        if (ids == null || ids.length == 0) {
            return new ArrayList<>();
        }

        List<ObjectId> objectIds = Arrays.stream(ids)
                .filter(java.util.Objects::nonNull)
                .filter(id -> id instanceof ReviewId) // Check for ReviewId
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
             logger.log("No valid ReviewIds provided in list query.", LogLevel.DEBUG);
            return new ArrayList<>();
        }

        Document query = new Document("_id", new Document("$in", objectIds));

        List<Document> docs = database.list(COLLECTION, query);
        logger.log("Retrieved " + docs.size() + " reviews from list query", LogLevel.DEBUG);
        return docs.stream()
                // Assuming DocumentConverter.documentToReview(doc) exists
                .map(DocumentConverter::documentToReview)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> search(String query) {
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

        logger.log("Searching for reviews with " + field + " = " + value, LogLevel.INFO);

        // Consider type conversion if searching non-string fields (e.g., rating)
        Object searchValue = value;
        if (field.equals("rating")) { // Example: convert rating to integer
            try {
                searchValue = Integer.valueOf(value);
            } catch (NumberFormatException e) {
                logger.log("Invalid rating value for search: " + value, LogLevel.WARNING);
                return new ArrayList<>();
            }
        }
        // Add more field-specific conversions if needed (e.g., userId, organizationId)

        Document searchQuery = new Document(field, searchValue);
        List<Document> docs = database.list(COLLECTION, searchQuery);

        if (docs == null || docs.isEmpty()) {
            logger.log("No reviews found for query: " + query, LogLevel.INFO);
            return new ArrayList<>();
        }

        return docs.stream()
                // Assuming DocumentConverter.documentToReview(doc) exists
                .map(DocumentConverter::documentToReview)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
} 