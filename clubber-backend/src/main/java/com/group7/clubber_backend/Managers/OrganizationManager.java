package com.group7.clubber_backend.Managers;

import java.util.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.group7.clubber_backend.Managers.base.Manager;

import com.group7.lib.types.Ids.AnnouncementId;
import com.group7.lib.types.Ids.EventId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ImageId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Ids.base.Id;

import com.group7.lib.types.Organization.Enums.Category;
import com.group7.lib.types.Organization.Enums.Importance;
import com.group7.lib.types.Organization.Enums.Visibility;

import com.group7.lib.types.Organization.AnnouncementObject;
import com.group7.lib.types.Organization.EventObject;
import com.group7.lib.types.Organization.Info;
import com.group7.lib.types.Organization.Organization;
import com.group7.lib.types.Organization.RecruitmentInfo;
import com.group7.lib.types.Organization.SocialMediaLinks;

import com.group7.lib.utilities.Database.Database;
import com.group7.lib.utilities.Database.DatabaseCollection;
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;

public class OrganizationManager extends Manager<Organization> {

    private static final OrganizationManager instance = new OrganizationManager();
    private final Database database;
    private final Logger logger;

    private OrganizationManager() {
        super();
        this.database = Database.getInstance();
        this.logger = new Logger("OrganizationManager");
    }

    // private MongoCollection<Document> collection;
    public static OrganizationManager getInstance() {
        return instance;
    }

    @Override
    public void create(Organization object) {
        this.logger.log("Creating organization", LogLevel.INFO);
        
        Document document = new Document()
                .append("name", object.getName())
                .append("id", object.getId())
                .append("category", object.getCategory())
                .append("tags", Arrays.stream(object.getTags())
        				.map(String::getValue)
        				.toList())
                .append("memberIds", Arrays.stream(object.getMemberIds())
        				.map(UserId::getValue)
        				.toList())
                .append("adminIds", Arrays.stream(object.getAdminIds())
        				.map(UserId::getValue)
        				.toList())
                .append("rating", object.getRating())
                .append("reviewIds", Arrays.stream(object.getReviewIds())
        				.map(ReviewId::getValue)
        				.toList())
                .append("recruitmentInfo", new ObjectId(object.getRecruitmentInfo()))
                .append("info", new ObjectId(object.getInfo()))
                .append("events", Arrays.stream(object.getEvents())
        				.map(EventObject::getValue)
        				.toList())
                .append("announcements", Arrays.stream(object.getAnnouncements())
        				.map(AnnouncementObject::getValue)
        				.toList())
                .append("logo", new ObjectId(object.getLogo()))
                .append("banner", new ObjectId(object.getBanner()))
                .append("visibility", new ObjectId(object.getVisibility()))
        
        this.database.insert(DatabaseCollection.ORGANIZATION, document);
    }

    @Override
    public void update(Organization object) {
        this.logger.log("Updating organization", LogLevel.INFO);
        
        if (object != null)
        {
        	Document doc = new Document()
        			.append("name", object.getName())
                    .append("id", object.getId())
                    .append("category", object.getCategory())
                    .append("tags", Arrays.stream(object.getTags())
        				.map(String::getValue)
        				.toList())
                    .append("memberIds", Arrays.stream(object.getMemberIds())
        				.map(UserId::getValue)
        				.toList())
                    .append("adminIds", Arrays.stream(object.getAdminIds())
        				.map(UserId::getValue)
        				.toList())
                    .append("rating", object.getRating())
                    .append("reviewIds", Arrays.stream(object.getReviewIds())
        				.map(ReviewId::getValue)
        				.toList())
                    .append("recruitmentInfo", new ObjectId(object.getRecruitmentInfo()))
                    .append("info", new ObjectId(object.getInfo()))
                    .append("events", Arrays.stream(object.getEvents())
        				.map(EventObject::getValue)
        				.toList())
                    .append("announcements", Arrays.stream(object.getAnnouncements())
        				.map(AnnouncementObject::getValue)
        				.toList())
                    .append("logo", new ObjectId(object.getLogo()))
                    .append("banner", new ObjectId(object.getBanner()))
                    .append("visibility", new ObjectId(object.getVisibility()))
        	
        	this.database.update(DatabaseCollection.ORGANIZATION, object.getId().getValue(), doc);
        	
        }
    }

    public void delete(Id id) {
        this.logger.log("Deleting organization", LogLevel.INFO);
        
        Organization organizationDelete = get(id);
        
        if (organizationDelete != null) {
        	String organizationID = organizationDelete.getId().getValue();
        	this.database.delete(DatabaseCollection.ORGANIZATION, organizationDelete);
        }
    }

    @Override
    public User get(Id id) {
        this.logger.log("Getting organization", LogLevel.INFO);
        
        Document doc = this.database.get(DatabaseCollection.ORGANIZATION, id.getValue());
        
        if (doc == null) {
        	this.logger.log("Organization not found for ID: " + id.getValue(), LogLevel.ERROR);
        }
        
        try {
        	return new Organization(
                    doc.getString("name"),
        			new OrganizationId(doc.getObjectId("id").toHexString()),
                    doc.getString("category"),
                    doc.getList("tags", String.class).stream()
        				.map(String::new)
        				.toArray(String[]::new),
                    doc.getList("memberIds", String.class).stream()
        				.map(UserId::new)
        				.toArray(UserId[]::new),
                    doc.getList("adminIds", String.class).stream()
        				.map(UserId::new)
        				.toArray(UserId[]::new),
                    doc.getDouble("rating"),
                    doc.getList("reviewIds", String.class).stream()
        				.map(ReviewId::new)
        				.toArray(ReviewId[]::new),
                    RecruitmentInfo.fromDocument(doc.get("recruitmentInfo", Document.class)),
                    Info.fromDocument(doc.get("info", Document.class)),
                    doc.getList("events", String.class).stream()
        				.map(EventObject::new)
        				.toArray(EventObject[]::new),
        			doc.getList("announcements", String.class).stream()
        				.map(AnnouncementObject::new)
        				.toArray(AnnouncementObject[]::new),
                    new ImageID(doc.getObjectId("logo").toHexString()),
                    new ImageID(doc.getObjectId("banner").toHexString()),
                    doc.getString("visibility")
        			);
        			
        } catch (Exception e) {
        	this.logger.log("Failed to parse organization document: " + e.getMessage(), LogLevel.ERROR);
        	return null;
        }
    }

    @Override
    public List<User> getAll() {
        this.logger.log("Getting all organizations", LogLevel.INFO);
        
        List<Document> docs = this.database.list(DatabaseCollection.ORGANIZATION, new Document());
        List<User> organizations = new ArrayList<>();
        
        for (Document doc : docs)
        {
        	try {
        		Organization organization = new Organization(
                    doc.getString("name"),
        			new OrganizationId(doc.getObjectId("id").toHexString()),
                    doc.getString("category"),
                    doc.getList("tags", String.class).stream()
        				.map(String::new)
        				.toArray(String[]::new),
                    doc.getList("memberIds", String.class).stream()
        				.map(UserId::new)
        				.toArray(UserId[]::new),
                    doc.getList("adminIds", String.class).stream()
        				.map(UserId::new)
        				.toArray(UserId[]::new),
                    doc.getDouble("rating"),
                    doc.getList("reviewIds", String.class).stream()
        				.map(ReviewId::new)
        				.toArray(ReviewId[]::new),
                    RecruitmentInfo.fromDocument(doc.get("recruitmentInfo", Document.class)),
                    Info.fromDocument(doc.get("info", Document.class)),
                    doc.getList("events", String.class).stream()
        				.map(EventObject::new)
        				.toArray(EventObject[]::new),
        			doc.getList("announcements", String.class).stream()
        				.map(AnnouncementObject::new)
        				.toArray(AnnouncementObject[]::new),
                    new ImageID(doc.getObjectId("logo").toHexString()),
                    new ImageID(doc.getObjectId("banner").toHexString()),
                    doc.getString("visibility"));
        		
        		organizations.add(organization);
        		
        	} catch (Exception e) {
        		this.logger.log("Error parsing organizations: " + e.getMessage(), LogLevel.ERROR);
        	}
        }
        
        return organizations;
    }

    @Override
    public void list(Id[] ids) {
        this.logger.log("Listing organizations", LogLevel.INFO);
        
        for (Id id : ids) {
        	Organization organization = get(id);
        	
        	if (organization != null) {
        		System.out.println("Organization: " + organization.getUsername() + " (" + organization.getEmail() + ")");
        	}
        	else {
        		System.out.println("Organization not found: " + id.getValue());
        	}
        }
        
        
    }
}