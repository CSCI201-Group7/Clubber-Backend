package com.group7.lib.utilities.Conversion;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.group7.lib.types.Announcement.Announcement;
import com.group7.lib.types.Comment.Comment;
import com.group7.lib.types.Event.Event;
import com.group7.lib.types.Ids.AnnouncementId;
import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.EventId;
import com.group7.lib.types.Ids.FileId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.types.Organization.Organization;
import com.group7.lib.types.Organization.OrganizationLinks;
import com.group7.lib.types.Organization.OrganizationType;
import com.group7.lib.types.Organization.RecruitingStatus;
import com.group7.lib.types.Review.Review;
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
                    } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException e) {
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
        doc.put("username", user.username());
        doc.put("password", user.password());
        doc.put("name", user.name());
        doc.put("email", user.email());
        doc.put("year", user.year() != null ? user.year().name() : null); // Store enum as string
        doc.put("reviewIds", idArrayToStringList(user.reviewIds()));
        doc.put("commentIds", idArrayToStringList(user.commentIds()));
        doc.put("contactIds", idArrayToStringList(user.contactIds()));

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
        UserId[] contactIds = stringListToIdArray(doc.getList("contactIds", String.class, new ArrayList<>()), UserId.class);

        // Use the User record's canonical constructor
        try {
            User user = new User(
                    userId,
                    username,
                    name,
                    email,
                    password,
                    year,
                    reviewIds,
                    commentIds,
                    contactIds,
                    null, // profileImageId
                    null // bio
            );
            return user;
        } catch (NullPointerException e) {
            logger.log("Error constructing User record. A required field (username, email, or password) might be null. Exception: " + e.getMessage(), LogLevel.ERROR);
            return null;
        }
    }

    // --- Organization Conversion ---
    public static Document organizationToDocument(Organization org) {
        if (org == null) {
            return null;
        }
        Document doc = new Document();
        // Let DB handle _id generation
        doc.put("name", org.name());
        doc.put("type", org.type() != null ? org.type().name() : null);
        doc.put("description", org.description());
        doc.put("contactEmail", org.contactEmail());
        doc.put("recruitingStatus", org.recruitingStatus() != null ? org.recruitingStatus().name() : null);
        doc.put("location", org.location());
        doc.put("links", organizationLinksToDocument(org.links()));

        // Convert List<Id> to List<String>
        doc.put("memberIds", idListToStringList(org.memberIds()));
        doc.put("adminIds", idListToStringList(org.adminIds()));
        doc.put("reviewIds", idListToStringList(org.reviewIds()));

        // Image and event IDs
        doc.put("profileImageId", org.profileImageId());
        doc.put("eventIds", org.eventIds());
        doc.put("announcementIds", idListToStringList(org.announcementIds()));

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

        String description = doc.getString("description");
        String contactEmail = doc.getString("contactEmail");
        String recruitingStatusStr = doc.getString("recruitingStatus");
        RecruitingStatus recruitingStatus = recruitingStatusStr != null ? RecruitingStatus.valueOf(recruitingStatusStr) : null;
        String location = doc.getString("location");
        OrganizationLinks links = documentToOrganizationLinks(doc.get("links", Document.class));

        // Convert List<String> back to List<Id>
        List<UserId> memberIds = stringListToIdList(doc.getList("memberIds", String.class, new ArrayList<>()), UserId.class);
        List<UserId> adminIds = stringListToIdList(doc.getList("adminIds", String.class, new ArrayList<>()), UserId.class);
        List<ReviewId> reviewIds = stringListToIdList(doc.getList("reviewIds", String.class, new ArrayList<>()), ReviewId.class);

        // Image and event IDs
        String profileImageId = doc.getString("profileImageId");
        List<String> eventIds = doc.getList("eventIds", String.class, new ArrayList<>());
        List<AnnouncementId> announcementIds = stringListToIdList(doc.getList("announcementIds", String.class, new ArrayList<>()), AnnouncementId.class);

        return new Organization(
                orgId,
                name,
                type,
                description,
                contactEmail,
                recruitingStatus,
                location,
                links,
                memberIds,
                adminIds,
                reviewIds,
                profileImageId,
                eventIds,
                announcementIds
        );
    }

    // OrganizationLinks conversion methods
    public static Document organizationLinksToDocument(OrganizationLinks links) {
        if (links == null) {
            return new Document();
        }
        Document doc = new Document();
        doc.put("website", links.website());
        doc.put("linkedIn", links.linkedIn());
        doc.put("instagram", links.instagram());
        doc.put("discord", links.discord());
        return doc;
    }

    public static OrganizationLinks documentToOrganizationLinks(Document doc) {
        if (doc == null) {
            return new OrganizationLinks(null, null, null, null);
        }
        return new OrganizationLinks(
                doc.getString("website"),
                doc.getString("linkedIn"),
                doc.getString("instagram"),
                doc.getString("discord")
        );
    }

    // --- Announcement Conversion ---
    public static Document announcementToDocument(Announcement announcement) {
        if (announcement == null) {
            return null;
        }
        Document doc = new Document();
        // ID is set by the database on creation, so not included here for new documents.
        // If updating, the ID would be used in the query, not in the document body typically.
        doc.put("organizationId", announcement.organizationId() != null ? announcement.organizationId().toString() : null);
        doc.put("authorId", announcement.authorId() != null ? announcement.authorId().toString() : null);
        doc.put("title", announcement.title());
        doc.put("content", announcement.content());
        doc.put("attachmentIds", idListToStringList(announcement.attachmentIds()));
        doc.put("createdAt", announcement.createdAt());
        doc.put("updatedAt", announcement.updatedAt());
        return doc;
    }

    public static Announcement documentToAnnouncement(Document doc) {
        if (doc == null) {
            return null;
        }

        ObjectId objectId = doc.getObjectId("_id");
        if (objectId == null) {
            logger.log("Announcement Document is missing _id field.", LogLevel.ERROR);
            return null;
        }
        AnnouncementId announcementId = new AnnouncementId(objectId.toHexString());

        String orgIdStr = doc.getString("organizationId");
        OrganizationId organizationId = orgIdStr != null ? new OrganizationId(orgIdStr) : null;

        String authorIdStr = doc.getString("authorId");
        UserId authorId = authorIdStr != null ? new UserId(authorIdStr) : null;

        String title = doc.getString("title");
        String content = doc.getString("content");

        // Robustly handle attachmentIds by using doc.get() and manual list processing
        Object rawAttachmentIdsField = doc.get("attachmentIds");
        List<String> stringAttachmentIds = new ArrayList<>();

        if (rawAttachmentIdsField instanceof List) {
            List<?> listFromDb = (List<?>) rawAttachmentIdsField;
            for (Object item : listFromDb) {
                if (item instanceof String string) {
                    stringAttachmentIds.add(string);
                } else if (item instanceof ObjectId objectId1) {
                    stringAttachmentIds.add(objectId1.toHexString());
                } else if (item != null) {
                    logger.log("Unexpected type in attachmentIds list for announcement " + announcementId.toString()
                            + ": Type=" + item.getClass().getName() + ", Value=" + item.toString() + ". Skipping.", LogLevel.WARNING);
                }
            }
        } else if (rawAttachmentIdsField != null) {
            // Log if attachmentIds is present but not a list
            logger.log("Field 'attachmentIds' is not a List for announcement " + announcementId.toString()
                    + ". Actual type: " + rawAttachmentIdsField.getClass().getName() + ". Treating as empty.", LogLevel.WARNING);
        }
        // If rawAttachmentIdsField is null, stringAttachmentIds remains empty.

        List<FileId> attachmentIds = stringListToIdList(stringAttachmentIds, FileId.class);

        String createdAt = doc.getString("createdAt");
        String updatedAt = doc.getString("updatedAt");

        return new Announcement(
                announcementId,
                organizationId,
                authorId,
                title,
                content,
                attachmentIds,
                createdAt,
                updatedAt
        );
    }

    // --- Rating Conversion ---
    public static Document ratingToDocument(Review.Rating rating) {
        if (rating == null) {
            return null;
        }
        Document doc = new Document();
        doc.put("overall", rating.overall());
        doc.put("community", rating.community());
        doc.put("activities", rating.activities());
        doc.put("leadership", rating.leadership());
        doc.put("inclusivity", rating.inclusivity());
        return doc;
    }

    public static Review.Rating documentToRating(Document doc) {
        if (doc == null) {
            return null;
        }
        return new Review.Rating(
                doc.getInteger("overall", 0), // Default to 0 if missing
                doc.getInteger("community", 0),
                doc.getInteger("activities", 0),
                doc.getInteger("leadership", 0),
                doc.getInteger("inclusivity", 0)
        );
    }

    // --- Review Conversion ---
    public static Document reviewToDocument(Review review) {
        if (review == null) {
            return null;
        }
        Document doc = new Document();
        // review.id() is usually handled by the database (_id) or set prior to insertion.
        // If you need to store it explicitly and it's not the MongoDB ObjectId:
        // if (review.id() != null) {
        //     doc.put("reviewId_str", review.id().toString());
        // }
        doc.put("authorId", review.authorId() != null ? review.authorId().toString() : null);
        doc.put("organizationId", review.organizationId() != null ? review.organizationId().toString() : null);
        doc.put("title", review.title());
        doc.put("content", review.content());
        doc.put("rating", ratingToDocument(review.rating())); // Use new ratingToDocument
        doc.put("fileIds", idListToStringList(review.fileIds())); // Use existing helper
        doc.put("createdAt", review.createdAt() != null ? Date.from(review.createdAt().toInstant(ZoneOffset.UTC)) : null);
        doc.put("updatedAt", review.updatedAt() != null ? Date.from(review.updatedAt().toInstant(ZoneOffset.UTC)) : null);
        doc.put("upvotes", idListToStringList(review.upvotes()));
        doc.put("downvotes", idListToStringList(review.downvotes()));
        doc.put("commentIds", idListToStringList(review.commentIds()));

        return doc;
    }

    public static Review documentToReview(Document doc) {
        if (doc == null) {
            return null;
        }

        ObjectId objectId = doc.getObjectId("_id");
        if (objectId == null) {
            logger.log("Review Document is missing _id field.", LogLevel.ERROR);
            // Attempt to get reviewId_str if _id is missing (if you decided to store it separately)
            // String reviewIdStr = doc.getString("reviewId_str");
            // if (reviewIdStr == null) {
            //    logger.log("Review Document is also missing reviewId_str field.", LogLevel.ERROR);
            //    return null;
            // }
            // reviewId = new ReviewId(reviewIdStr);
            return null; // Strict: _id must be present for conversion
        }
        ReviewId reviewId = new ReviewId(objectId.toHexString());

        String authorIdStr = doc.getString("authorId");
        UserId authorId = authorIdStr != null ? new UserId(authorIdStr) : null;

        String organizationIdStr = doc.getString("organizationId");
        OrganizationId organizationId = organizationIdStr != null ? new OrganizationId(organizationIdStr) : null;

        String title = doc.getString("title");
        String content = doc.getString("content");

        Document ratingDoc = doc.get("rating", Document.class);
        Review.Rating rating = documentToRating(ratingDoc); // Use new documentToRating

        List<FileId> fileIds = stringListToIdList(doc.getList("fileIds", String.class, new ArrayList<>()), FileId.class);

        Date createdAtDate = doc.getDate("createdAt");
        LocalDateTime createdAt = createdAtDate != null ? LocalDateTime.ofInstant(createdAtDate.toInstant(), ZoneOffset.UTC) : null;

        Date updatedAtDate = doc.getDate("updatedAt");
        LocalDateTime updatedAt = updatedAtDate != null ? LocalDateTime.ofInstant(updatedAtDate.toInstant(), ZoneOffset.UTC) : null;

        List<UserId> upvotes = stringListToIdList(doc.getList("upvotes", String.class, new ArrayList<>()), UserId.class);
        List<UserId> downvotes = stringListToIdList(doc.getList("downvotes", String.class, new ArrayList<>()), UserId.class);

        List<CommentId> commentIds = stringListToIdList(doc.getList("commentIds", String.class, new ArrayList<>()), CommentId.class);

        return new Review(
                reviewId,
                authorId,
                organizationId,
                title,
                content,
                rating,
                fileIds,
                createdAt,
                updatedAt,
                upvotes,
                downvotes,
                commentIds);
    }

    // --- Comment Conversion ---
    public static Document commentToDocument(Comment comment) {
        if (comment == null) {
            return null;
        }
        Document doc = new Document();
        // comment.id() is handled by DB.
        doc.put("userId", comment.userId() != null ? comment.userId().toString() : null);
        doc.put("reviewId", comment.reviewId() != null ? comment.reviewId().toString() : null);
        doc.put("text", comment.text());
        doc.put("createdAt", comment.createdAt() != null ? Date.from(comment.createdAt().toInstant(ZoneOffset.UTC)) : null);
        doc.put("updatedAt", comment.updatedAt() != null ? Date.from(comment.updatedAt().toInstant(ZoneOffset.UTC)) : null);
        return doc;
    }

    public static Comment documentToComment(Document doc) {
        if (doc == null) {
            return null;
        }

        ObjectId objectId = doc.getObjectId("_id");
        if (objectId == null) {
            logger.log("Comment Document is missing _id field.", LogLevel.ERROR);
            return null;
        }
        CommentId commentId = new CommentId(objectId.toHexString());

        String userIdStr = doc.getString("userId");
        UserId userId = userIdStr != null ? new UserId(userIdStr) : null;

        String reviewIdStr = doc.getString("reviewId");
        ReviewId reviewId = reviewIdStr != null ? new ReviewId(reviewIdStr) : null;

        String text = doc.getString("text");

        Date createdAtDate = doc.getDate("createdAt");
        LocalDateTime createdAt = createdAtDate != null ? LocalDateTime.ofInstant(createdAtDate.toInstant(), ZoneOffset.UTC) : null;

        Date updatedAtDate = doc.getDate("updatedAt");
        LocalDateTime updatedAt = updatedAtDate != null ? LocalDateTime.ofInstant(updatedAtDate.toInstant(), ZoneOffset.UTC) : null;

        return new Comment(
                commentId,
                userId,
                reviewId,
                text,
                createdAt,
                updatedAt
        );
    }

    // --- Event Conversion ---
    public static Document eventToDocument(Event event) {
        if (event == null) {
            return null;
        }
        Document doc = new Document();
        // Event ID is handled by the database on creation (if event.id() is null or represents a new event).
        // If event.id() is not null, it implies an update, but we don't set _id in the update document body itself.
        // The ID is used in the query for update operations.
        doc.put("organizationId", event.organizationId() != null ? event.organizationId().toString() : null);
        doc.put("title", event.title());
        doc.put("description", event.description());
        doc.put("location", event.location());
        doc.put("startTime", event.startTime()); // Directly store java.util.Date, MongoDB driver handles BSON Date
        doc.put("endTime", event.endTime());   // Directly store java.util.Date
        doc.put("rsvpLink", event.rsvpLink());
        doc.put("attachmentIds", idListToStringList(event.attachmentIds())); // Convert List<FileId> to List<String>

        // Note: Fields like authorId, isOnline, tags, coverImageId, maxCapacity, visibility, rsvpUserIds, waitlistUserIds,
        // createdAt, updatedAt were removed from EventController based on Event record definition.
        // If they are ever re-added to the Event record, they should be handled here.
        return doc;
    }

    public static Event documentToEvent(Document doc) {
        if (doc == null) {
            return null;
        }

        ObjectId objectId = doc.getObjectId("_id");
        if (objectId == null) {
            logger.log("Event Document is missing _id field.", LogLevel.ERROR);
            return null;
        }
        EventId eventId = new EventId(objectId.toHexString());

        String orgIdStr = doc.getString("organizationId");
        OrganizationId organizationId = orgIdStr != null ? new OrganizationId(orgIdStr) : null;

        String title = doc.getString("title");
        String description = doc.getString("description");
        String location = doc.getString("location");

        String startTime = doc.getString("startTime");
        String endTime = doc.getString("endTime");

        String rsvpLink = doc.getString("rsvpLink");

        List<String> stringAttachmentIds = doc.getList("attachmentIds", String.class, new ArrayList<>());
        List<FileId> attachmentIds = stringListToIdList(stringAttachmentIds, FileId.class); // Convert List<String> to List<FileId>

        return new Event(
                eventId,
                organizationId,
                title,
                description,
                location,
                startTime,
                endTime,
                rsvpLink,
                attachmentIds
        );
    }
}
