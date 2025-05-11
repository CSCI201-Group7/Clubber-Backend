package com.group7.clubber_backend.Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.group7.clubber_backend.Managers.base.Manager;
import com.group7.lib.types.Event.Event;
import com.group7.lib.types.Ids.EventId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.utilities.Conversion.DocumentConverter;
import com.group7.lib.utilities.Database.Database;
import com.group7.lib.utilities.Database.DatabaseCollection;
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;

public class EventManager extends Manager<Event> {

    private static final EventManager instance = new EventManager();
    private final Database database;
    private final Logger logger;
    private static final DatabaseCollection COLLECTION = DatabaseCollection.EVENT; // Assuming EVENT enum exists or will be added

    private EventManager() {
        super();
        this.database = Database.getInstance();
        this.logger = new Logger("Managers/Event");
    }

    public static EventManager getInstance() {
        return instance;
    }

    @Override
    public Id create(Event event) {
        if (event == null) {
            logger.log("Attempted to create a null event", LogLevel.WARNING);
            return null;
        }
        if (event.id() != null && event.id().toString() != null && !event.id().toString().equals("null")) {
            logger.log("Event object already has an ID (" + event.id().toString() + ") before creation.", LogLevel.WARNING);
        }
        // Assuming DocumentConverter.eventToDocument(event) will exist
        Document doc = DocumentConverter.eventToDocument(event);
        if (doc == null) {
            logger.log("Failed to convert event to document for creation.", LogLevel.ERROR);
            return null;
        }
        String newId = database.insert(COLLECTION, doc);
        if (newId != null) {
            logger.log("Created event with ID: " + newId, LogLevel.INFO);
            return new EventId(newId);
        } else {
            logger.log("Failed to create event: " + event.title(), LogLevel.ERROR);
            return null;
        }
    }

    @Override
    public void update(Event event) {
        if (event == null || event.id() == null) {
            logger.log("Attempted to update an event with null object or null ID", LogLevel.WARNING);
            return;
        }
        // Assuming DocumentConverter.eventToDocument(event) will exist
        Document doc = DocumentConverter.eventToDocument(event);
        if (doc == null) {
            logger.log("Failed to convert event to document for update.", LogLevel.ERROR);
            return;
        }
        doc.remove("_id"); // ID is immutable or managed by database

        boolean success = database.update(COLLECTION, event.id().toString(), doc);
        if (success) {
            logger.log("Updated event with ID: " + event.id(), LogLevel.INFO);
        } else {
            logger.log("Failed to update event with ID: " + event.id() + ". It might not exist.", LogLevel.WARNING);
        }
    }

    @Override
    public void delete(Id id) {
        if (id == null) {
            logger.log("Attempted to delete an event with null ID", LogLevel.WARNING);
            return;
        }
        String eventId = id.toString();
        boolean success = database.delete(COLLECTION, eventId);
        if (success) {
            logger.log("Deleted event with ID: " + eventId, LogLevel.INFO);
        } else {
            logger.log("Failed to delete event with ID: " + eventId + ". It might not exist.", LogLevel.WARNING);
        }
    }

    @Override
    public Event get(Id id) {
        if (id == null) {
            logger.log("Attempted to get an event with null ID", LogLevel.WARNING);
            return null;
        }
        String eventId = id.toString();
        Document doc = database.get(COLLECTION, eventId);
        if (doc != null) {
            logger.log("Retrieved event with ID: " + eventId, LogLevel.DEBUG);
            // Assuming DocumentConverter.documentToEvent(doc) will exist
            return DocumentConverter.documentToEvent(doc);
        } else {
            logger.log("Event not found with ID: " + eventId, LogLevel.INFO);
            return null;
        }
    }

    @Override
    public List<Event> getAll() {
        List<Document> docs = database.list(COLLECTION, new Document());
        logger.log("Retrieved " + docs.size() + " events", LogLevel.DEBUG);
        return docs.stream()
                // Assuming DocumentConverter.documentToEvent(doc) will exist
                .map(DocumentConverter::documentToEvent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> list(Id[] ids) {
        if (ids == null || ids.length == 0) {
            return new ArrayList<>();
        }

        List<ObjectId> objectIds = Arrays.stream(ids)
                .filter(Objects::nonNull)
                .filter(id -> id instanceof EventId)
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
            logger.log("No valid EventIds provided in list query.", LogLevel.DEBUG);
            return new ArrayList<>();
        }

        Document query = new Document("_id", new Document("$in", objectIds));

        List<Document> docs = database.list(COLLECTION, query);
        logger.log("Retrieved " + docs.size() + " events from list query", LogLevel.DEBUG);
        return docs.stream()
                // Assuming DocumentConverter.documentToEvent(doc) will exist
                .map(DocumentConverter::documentToEvent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> search(String query) {
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

        logger.log("Searching for events with " + field + " = " + value, LogLevel.INFO);
        
        Object searchValue = value;
        // Add type conversions if necessary for specific fields, e.g., dates, numbers
        // if (field.equals("someNumericField")) {
        //     try {
        //         searchValue = Integer.valueOf(value);
        //     } catch (NumberFormatException e) {
        //         logger.log("Invalid numeric value for search: " + value, LogLevel.WARNING);
        //         return new ArrayList<>();
        //     }
        // }

        Document searchQuery = new Document(field, searchValue);
        List<Document> docs = database.list(COLLECTION, searchQuery);

        if (docs == null || docs.isEmpty()) {
            logger.log("No events found for query: " + query, LogLevel.INFO);
            return new ArrayList<>();
        }

        return docs.stream()
                // Assuming DocumentConverter.documentToEvent(doc) will exist
                .map(DocumentConverter::documentToEvent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Event> getByOrganizationId(OrganizationId orgId) {
        if (orgId == null) {
            logger.log("Attempted to get events with null OrganizationId", LogLevel.WARNING);
            return new ArrayList<>();
        }
        Document query = new Document("organizationId", orgId.toString()); // Assuming Event stores organizationId as string
        List<Document> docs = database.list(COLLECTION, query);
        logger.log("Retrieved " + docs.size() + " events for organization ID: " + orgId.toString(), LogLevel.DEBUG);
        return docs.stream()
                .map(DocumentConverter::documentToEvent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
} 