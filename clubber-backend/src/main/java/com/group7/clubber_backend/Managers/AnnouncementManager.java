package com.group7.clubber_backend.Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.group7.clubber_backend.Managers.base.Manager;
import com.group7.lib.types.Announcement.Announcement;
import com.group7.lib.types.Ids.AnnouncementId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.utilities.Conversion.DocumentConverter;
import com.group7.lib.utilities.Database.Database;
import com.group7.lib.utilities.Database.DatabaseCollection;
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;

public class AnnouncementManager extends Manager<Announcement> {

    private static final AnnouncementManager instance = new AnnouncementManager();
    private final Database database;
    private final Logger logger;
    private static final DatabaseCollection COLLECTION = DatabaseCollection.ANNOUNCEMENT;

    private AnnouncementManager() {
        super();
        this.database = Database.getInstance();
        this.logger = new Logger("Managers/Announcement");
    }

    public static AnnouncementManager getInstance() {
        return instance;
    }

    @Override
    public Id create(Announcement announcement) {
        if (announcement == null) {
            logger.log("Attempted to create a null announcement", LogLevel.WARNING);
            return null;
        }
        if (announcement.id() != null) {
            logger.log("Announcement object already has an ID (" + announcement.id().toString() + ") before creation.", LogLevel.WARNING);
        }
        Document doc = DocumentConverter.announcementToDocument(announcement);
        if (doc == null) {
            logger.log("Failed to convert announcement to document for creation.", LogLevel.ERROR);
            return null;
        }
        String newId = database.insert(COLLECTION, doc);
        if (newId != null) {
            logger.log("Created announcement with ID: " + newId, LogLevel.INFO);
            return new AnnouncementId(newId);
        } else {
            logger.log("Failed to create announcement: " + announcement.title(), LogLevel.ERROR);
            return null;
        }
    }

    @Override
    public void update(Announcement announcement) {
        if (announcement == null || announcement.id() == null) {
            logger.log("Attempted to update an announcement with null object or null ID", LogLevel.WARNING);
            return;
        }
        Document doc = DocumentConverter.announcementToDocument(announcement);
        if (doc == null) {
            logger.log("Failed to convert announcement to document for update.", LogLevel.ERROR);
            return;
        }
        doc.remove("_id"); // ID is immutable or managed by database, not updated in the document body

        boolean success = database.update(COLLECTION, announcement.id().toString(), doc);
        if (success) {
            logger.log("Updated announcement with ID: " + announcement.id().toString(), LogLevel.INFO);
        } else {
            logger.log("Failed to update announcement with ID: " + announcement.id().toString() + ". It might not exist.", LogLevel.WARNING);
        }
    }

    @Override
    public void delete(Id id) {
        if (id == null) {
            logger.log("Attempted to delete an announcement with null ID", LogLevel.WARNING);
            return;
        }
        String announcementId = id.toString();
        boolean success = database.delete(COLLECTION, announcementId);
        if (success) {
            logger.log("Deleted announcement with ID: " + announcementId, LogLevel.INFO);
        } else {
            logger.log("Failed to delete announcement with ID: " + announcementId + ". It might not exist.", LogLevel.WARNING);
        }
    }

    @Override
    public Announcement get(Id id) {
        if (id == null) {
            logger.log("Attempted to get an announcement with null ID", LogLevel.WARNING);
            return null;
        }
        String announcementId = id.toString();
        Document doc = database.get(COLLECTION, announcementId);
        if (doc != null) {
            logger.log("Retrieved announcement with ID: " + announcementId, LogLevel.DEBUG);
            return DocumentConverter.documentToAnnouncement(doc);
        } else {
            logger.log("Announcement not found with ID: " + announcementId, LogLevel.INFO);
            return null;
        }
    }

    @Override
    public List<Announcement> getAll() {
        List<Document> docs = database.list(COLLECTION, new Document());
        logger.log("Retrieved " + docs.size() + " announcements", LogLevel.DEBUG);
        return docs.stream()
                .map(DocumentConverter::documentToAnnouncement)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Announcement> list(Id[] ids) {
        if (ids == null || ids.length == 0) {
            return new ArrayList<>();
        }

        List<ObjectId> objectIds = Arrays.stream(ids)
                .filter(Objects::nonNull)
                .filter(id -> id instanceof AnnouncementId)
                .map(id -> {
                    try {
                        return new ObjectId(id.toString());
                    } catch (IllegalArgumentException e) {
                        logger.log("Invalid ObjectId format in list: " + id.toString(), LogLevel.WARNING);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (objectIds.isEmpty()) {
            logger.log("No valid AnnouncementIds provided in list query.", LogLevel.DEBUG);
            return new ArrayList<>();
        }

        Document query = new Document("_id", new Document("$in", objectIds));

        List<Document> docs = database.list(COLLECTION, query);
        logger.log("Retrieved " + docs.size() + " announcements from list query", LogLevel.DEBUG);
        return docs.stream()
                .map(DocumentConverter::documentToAnnouncement)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Announcement> search(String query) {
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

        logger.log("Searching for announcements with " + field + " = " + value, LogLevel.INFO);

        Document searchQuery = new Document(field, value);
        // Special handling for searching by organizationId string
        if ("organizationId".equals(field)) {
            // Assuming value is the string representation of OrganizationId
            searchQuery = new Document(field, value);
        }
        
        List<Document> docs = database.list(COLLECTION, searchQuery);

        if (docs == null || docs.isEmpty()) {
            logger.log("No announcements found for query: " + query, LogLevel.INFO);
            return new ArrayList<>();
        }

        return docs.stream()
                .map(DocumentConverter::documentToAnnouncement)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Announcement> getByOrganizationId(OrganizationId orgId) {
        if (orgId == null) {
            logger.log("Attempted to get announcements with null OrganizationId", LogLevel.WARNING);
            return new ArrayList<>();
        }
        Document query = new Document("organizationId", orgId.toString());
        List<Document> docs = database.list(COLLECTION, query);
        logger.log("Retrieved " + docs.size() + " announcements for organization ID: " + orgId.toString(), LogLevel.DEBUG);
        return docs.stream()
                .map(DocumentConverter::documentToAnnouncement)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
} 