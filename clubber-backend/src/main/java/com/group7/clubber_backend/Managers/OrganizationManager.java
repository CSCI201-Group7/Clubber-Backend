package com.group7.clubber_backend.Managers;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.group7.clubber_backend.Managers.base.Manager;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Organization.Organization;
import com.group7.lib.utilities.Database.Database;
import com.group7.lib.utilities.Database.DatabaseCollection;
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;
import com.group7.lib.utilities.Conversion.DocumentConverter;

public class OrganizationManager extends Manager<Organization> {

    private static final OrganizationManager instance = new OrganizationManager();
    private final Database database;
    private final Logger logger;
    private static final DatabaseCollection COLLECTION = DatabaseCollection.ORGANIZATION;

    private OrganizationManager() {
        super();
        this.database = Database.getInstance();
        this.logger = new Logger("Managers/Organization");
    }

    public static OrganizationManager getInstance() {
        return instance;
    }

    @Override
    public void create(Organization org) {
        if (org == null) {
            logger.log("Attempted to create a null organization", LogLevel.WARNING);
            return;
        }
        if (org.getId() != null) {
            logger.log("Organization object already has an ID (" + org.getId().toString() + ") before creation.", LogLevel.WARNING);
        }
        Document doc = DocumentConverter.organizationToDocument(org);
        if (doc == null) {
             logger.log("Failed to convert organization to document for creation.", LogLevel.ERROR);
             return;
         }
        String newId = database.insert(COLLECTION, doc);
        if (newId != null) {
             logger.log("Created organization with ID: " + newId, LogLevel.INFO);
        } else {
            logger.log("Failed to create organization: " + org.getName(), LogLevel.ERROR);
        }
    }

    @Override
    public void update(Organization org) {
        if (org == null || org.getId() == null) {
            logger.log("Attempted to update an organization with null object or null ID", LogLevel.WARNING);
            return;
        }
        Document doc = DocumentConverter.organizationToDocument(org);
         if (doc == null) {
             logger.log("Failed to convert organization to document for update.", LogLevel.ERROR);
             return;
         }
        doc.remove("_id");

        boolean success = database.update(COLLECTION, org.getId().toString(), doc);
        if (success) {
            logger.log("Updated organization with ID: " + org.getId().toString(), LogLevel.INFO);
        } else {
            logger.log("Failed to update organization with ID: " + org.getId().toString() + ". It might not exist.", LogLevel.WARNING);
        }
    }

    @Override
    public void delete(Id id) {
        if (id == null) {
            logger.log("Attempted to delete an organization with null ID", LogLevel.WARNING);
            return;
        }
        String orgId = id.toString();
        boolean success = database.delete(COLLECTION, orgId);
        if (success) {
            logger.log("Deleted organization with ID: " + orgId, LogLevel.INFO);
        } else {
            logger.log("Failed to delete organization with ID: " + orgId + ". It might not exist.", LogLevel.WARNING);
        }
    }

    @Override
    public Organization get(Id id) {
        if (id == null) {
            logger.log("Attempted to get an organization with null ID", LogLevel.WARNING);
            return null;
        }
        String orgId = id.toString();
        Document doc = database.get(COLLECTION, orgId);
        if (doc != null) {
            logger.log("Retrieved organization with ID: " + orgId, LogLevel.DEBUG);
            return DocumentConverter.documentToOrganization(doc);
        } else {
            logger.log("Organization not found with ID: " + orgId, LogLevel.INFO);
            return null;
        }
    }

    @Override
    public List<Organization> getAll() {
        List<Document> docs = database.list(COLLECTION, new Document());
        logger.log("Retrieved " + docs.size() + " organizations", LogLevel.DEBUG);
        return docs.stream()
                .map(DocumentConverter::documentToOrganization)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Organization> list(Id[] ids) {
        if (ids == null || ids.length == 0) {
            return new ArrayList<>();
        }

        List<ObjectId> objectIds = Arrays.stream(ids)
                .filter(java.util.Objects::nonNull)
                .filter(id -> id instanceof OrganizationId)
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
             logger.log("No valid OrganizationIds provided in list query.", LogLevel.DEBUG);
            return new ArrayList<>();
        }

        Document query = new Document("_id", new Document("$in", objectIds));

        List<Document> docs = database.list(COLLECTION, query);
        logger.log("Retrieved " + docs.size() + " organizations from list query", LogLevel.DEBUG);
        return docs.stream()
                .map(DocumentConverter::documentToOrganization)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}
