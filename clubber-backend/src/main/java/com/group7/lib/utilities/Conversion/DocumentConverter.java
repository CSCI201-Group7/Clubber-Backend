package com.group7.lib.utilities.Conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId; // Assuming logger might be needed later or passed
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.types.Organization.Organization;
import com.group7.lib.types.Organization.OrganizationInfo;
import com.group7.lib.types.Organization.OrganizationType;
import com.group7.lib.types.User.User;
import com.group7.lib.types.User.Year;
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;

public class DocumentConverter {

    // Static logger instance (optional, could be passed if needed)
    private static final Logger logger = new Logger("DocumentConverter");

    // --- Generic ID List Converters ---
    /**
     * Converts an array of Id objects to a List of their String
     * representations.
     */
    public static <T extends Id> List<String> idArrayToStringList(T[] ids) {
        if (ids == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(ids)
                .map(Id::toString) // Assumes Id::toString returns the hex string
                .collect(Collectors.toList());
    }

    /**
     * Converts a List of Id objects to a List of their String representations.
     */
    public static <T extends Id> List<String> idListToStringList(List<T> ids) {
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(Id::toString) // Assumes Id::toString returns the hex string
                .collect(Collectors.toList());
    }

    /**
     * Converts a List of String IDs back to an array of a specific Id subtype.
     * Requires Id subtypes to have a constructor accepting a String ID.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Id> T[] stringListToIdArray(List<String> stringIds, Class<T> idClass) {
        if (stringIds == null) {
            return (T[]) java.lang.reflect.Array.newInstance(idClass, 0);
        }
        List<T> list = stringListToIdList(stringIds, idClass); // Reuse list conversion
        return list.toArray((T[]) java.lang.reflect.Array.newInstance(idClass, list.size()));
    }

    /**
     * Converts a List of String IDs back to a List of a specific Id subtype.
     * Requires Id subtypes to have a constructor accepting a String ID.
     */
    public static <T extends Id> List<T> stringListToIdList(List<String> stringIds, Class<T> idClass) {
        if (stringIds == null) {
            return new ArrayList<>();
        }
        return stringIds.stream()
                .map(strId -> {
                    try {
                        // Use reflection to call the constructor that accepts a String
                        java.lang.reflect.Constructor<T> constructor = idClass.getDeclaredConstructor(String.class);
                        return constructor.newInstance(strId);
                    } catch (NoSuchMethodException e) {
                        logger.log("ID class " + idClass.getSimpleName() + " does not have a String constructor.", LogLevel.ERROR);
                        return null;
                    } catch (Exception e) {
                        logger.log("Error converting string to ID (" + idClass.getSimpleName() + "): " + strId + ", Error: " + e.getMessage(), LogLevel.ERROR);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // --- User Conversion ---
    public static Document userToDocument(User user) {
        if (user == null) {
            return null;
        }
        Document doc = new Document();
        // User ID is final, assuming it's set correctly before calling this
        // or handled by DB insertion (_id usually set by DB driver)
        // doc.put("_id", new ObjectId(user.getId().toString())); // Usually not needed here
        doc.put("username", user.getUsername());
        doc.put("password", user.getPassword());
        doc.put("name", user.getName());
        doc.put("email", user.getEmail());
        doc.put("year", user.getYear() != null ? user.getYear().name() : null); // Store enum as string
        doc.put("reviewIds", idArrayToStringList(user.getReviewIds()));
        doc.put("commentIds", idArrayToStringList(user.getCommentIds()));
        doc.put("organizationIds", idArrayToStringList(user.getOrganizationIds()));
        doc.put("contactIds", idArrayToStringList(user.getContactIds()));

        return doc;
    }

    public static User documentToUser(Document doc) {
        if (doc == null) {
            return null;
        }

        // Ensure _id exists and is an ObjectId before conversion
        ObjectId objectId = doc.getObjectId("_id");
        if (objectId == null) {
            logger.log("Document is missing _id field.", LogLevel.ERROR);
            return null;
        }
        UserId userId = new UserId(objectId.toHexString()); // Convert ObjectId to String for constructor

        String username = doc.getString("username");
        String name = doc.getString("name");
        String email = doc.getString("email");
        String yearStr = doc.getString("year");
        String password = doc.getString("password");
        Year year = (yearStr != null) ? Year.valueOf(yearStr) : null; // Convert string back to enum, handle null

        // Convert lists of strings back to Id arrays
        ReviewId[] reviewIds = stringListToIdArray(doc.getList("reviewIds", String.class, new ArrayList<>()), ReviewId.class);
        CommentId[] commentIds = stringListToIdArray(doc.getList("commentIds", String.class, new ArrayList<>()), CommentId.class);
        OrganizationId[] organizationIds = stringListToIdArray(doc.getList("organizationIds", String.class, new ArrayList<>()), OrganizationId.class);
        UserId[] contactIds = stringListToIdArray(doc.getList("contactIds", String.class, new ArrayList<>()), UserId.class);

        // Use the User constructor
        // Note: If Favorites is null due to placeholder, User constructor might fail if it requires non-null.
        try {
            User user = new User(username, email, password);
            user.setId(userId);
            user.setReviewIds(reviewIds);
            user.setCommentIds(commentIds);
            user.setOrganizationIds(organizationIds);
            user.setContactIds(contactIds);
            user.setYear(year);
            user.setName(name);
            return user;
        } catch (NullPointerException e) {
            logger.log("Error constructing User, possibly due to null Favorites placeholder: " + e.getMessage(), LogLevel.ERROR);
            return null; // Or handle differently
        }
    }

    // --- Organization Conversion ---
    public static Document organizationToDocument(Organization org) {
        if (org == null) {
            return null;
        }
        Document doc = new Document();
        // Let DB handle _id generation
        doc.put("name", org.getName());
        doc.put("type", org.getType() != null ? org.getType().name() : null);
        doc.put("info", organizationInfoToDocument(org.getInfo())); // Placeholder
        // Convert List<Id> to List<String>
        doc.put("memberIds", idListToStringList(org.getMemberIds()));
        doc.put("adminIds", idListToStringList(org.getAdminIds()));
        doc.put("reviewIds", idListToStringList(org.getReviewIds()));
        // Assuming these are already String IDs or handled elsewhere
        doc.put("profileImageId", org.getProfileImageId());
        doc.put("eventIds", org.getEventIds()); // Removed idListToStringList call, assuming getEventIds returns List<String>
        doc.put("announcementIds", org.getAnnouncementIds()); // Assuming List<String>
        doc.put("bannerImageId", org.getBannerImageId());
        return doc;
    }

    public static Organization documentToOrganization(Document doc) {
        if (doc == null) {
            return null;
        }

        ObjectId objectId = doc.getObjectId("_id");
        if (objectId == null) {
            logger.log("Organization Document is missing _id field.", LogLevel.ERROR);
            return null;
        }
        OrganizationId orgId = new OrganizationId(objectId.toHexString());

        String name = doc.getString("name");
        String typeStr = doc.getString("type");
        OrganizationType type = typeStr != null ? OrganizationType.valueOf(typeStr) : null;

        OrganizationInfo info = documentToOrganizationInfo(doc.get("info", Document.class)); // Placeholder

        // Convert List<String> back to List<Id>
        List<UserId> memberIds = stringListToIdList(doc.getList("memberIds", String.class, new ArrayList<>()), UserId.class);
        List<UserId> adminIds = stringListToIdList(doc.getList("adminIds", String.class, new ArrayList<>()), UserId.class);
        List<ReviewId> reviewIds = stringListToIdList(doc.getList("reviewIds", String.class, new ArrayList<>()), ReviewId.class);
        List<String> eventIds = doc.getList("eventIds", String.class, new ArrayList<>()); // Directly get list of strings

        // Assuming these are stored as Strings
        String profileImageId = doc.getString("profileImageId");
        List<String> announcementIds = doc.getList("announcementIds", String.class, new ArrayList<>());
        String bannerImageId = doc.getString("bannerImageId");

        // Construct Organization object
        // Note: May fail if Info is null and constructor requires non-null
        try {
            return new Organization(orgId, name, type, info, memberIds, adminIds, reviewIds,
                    profileImageId, eventIds, announcementIds, bannerImageId);
        } catch (NullPointerException e) {
            logger.log("Error constructing Organization, possibly due to null Info placeholder: " + e.getMessage(), LogLevel.ERROR);
            return null;
        }
    }

    // Placeholder for OrganizationInfo conversion
    public static Document organizationInfoToDocument(OrganizationInfo info) {
        // TODO: Implement based on OrganizationInfo structure
        if (info == null) {
            return new Document();
        }
        logger.log("OrganizationInfo to Document conversion not fully implemented.", LogLevel.WARNING);
        return new Document("placeholder", "organization info data");
    }

    // Placeholder for Document to OrganizationInfo conversion
    public static OrganizationInfo documentToOrganizationInfo(Document infoDoc) {
        // TODO: Implement based on OrganizationInfo structure
        if (infoDoc == null || infoDoc.isEmpty() || infoDoc.containsKey("placeholder")) {
            logger.log("Document to OrganizationInfo conversion used placeholder logic.", LogLevel.DEBUG);
            return null; // Adjust if Org constructor needs non-null Info
        }
        logger.log("Document to OrganizationInfo conversion needs implementation.", LogLevel.WARNING);
        return null; // Needs real implementation
    }

}
