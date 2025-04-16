package com.group7.clubber_backend.Managers;

import java.util.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.group7.clubber_backend.Managers.base.Manager;
import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.types.User.Favorites;
import com.group7.lib.types.User.User;
import com.group7.lib.types.User.Year;
import com.group7.lib.utilities.Database.Database;
import com.group7.lib.utilities.Database.DatabaseCollection;
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;

public class UserManager extends Manager<User> {

    private static final UserManager instance = new UserManager();
    private final Database database;
    private final Logger logger;

    private UserManager() {
        super();
        this.database = Database.getInstance();
        this.logger = new Logger("UserManager");
    }

    // private MongoCollection<Document> collection;
    public static UserManager getInstance() {
        return instance;
    }

    @Override
    public void create(User object) {
        this.logger.log("Creating user", LogLevel.INFO);
        
        Document document = new Document()
        		.append("_id", new ObjectId(object.getId().getValue()))
        		.append("username", object.getUsername())
        		.append("name", object.getName())
        		.append("email", object.getEmail())
        		.append("year", object.getYear().getDisplayName())
        		.append("reviewIds", Arrays.stream(object.getReviewIds())
        				.map(ReviewId::getValue)
        				.toList())
        		.append("commentIds", Arrays.stream(object.getCommentIds())
        				.map(CommentId::getValue)
        				.toList())
        		.append("organizationIds", Arrays.stream(object.getOrganizationIds())
        				.map(OrganizationId::getValue)
        				.toList())
        		.append("contactIds", Arrays.stream(object.getContactIds())
        				.map(UserId::getValue)
        				.toList())
        		.append("favorites", object.getFavorites().toDocument());
        
        this.database.insert(DatabaseCollection.USER, document);
    }

    @Override
    public void update(User object) {
        this.logger.log("Updating user", LogLevel.INFO);
        
        if (object != null)
        {
        	Document doc = new Document()
        			.append("_id", new ObjectId(object.getId().getValue()))
        			.append("username", object.getUsername())
        			.append("name", object.getName())
        			.append("email", object.getEmail())
            		.append("year", object.getYear().getDisplayName())
            		.append("reviewIds", Arrays.stream(object.getReviewIds())
            				.map(ReviewId::getValue)
            				.toList())
            		.append("commentIds", Arrays.stream(object.getCommentIds())
            				.map(CommentId::getValue)
            				.toList())
            		.append("organizationIds", Arrays.stream(object.getOrganizationIds())
            				.map(OrganizationId::getValue)
            				.toList())
            		.append("contactIds", Arrays.stream(object.getContactIds())
            				.map(UserId::getValue)
            				.toList())
            		.append("favorites", object.getFavorites().toDocument());
        	
        	this.database.update(DatabaseCollection.USER, object.getId().getValue(), doc);
        	
        }
    }

    public void delete(Id id) {
        this.logger.log("Deleting user", LogLevel.INFO);
        
        User userDelete = get(id);
        
        // check to see if ID belongs to a valid user
        if (userDelete != null) {
        	String userID = userDelete.getId().getValue();
        	this.database.delete(DatabaseCollection.USER, userID);
        }
    }

    @Override
    public User get(Id id) {
        this.logger.log("Getting user", LogLevel.INFO);
        
        // get user data from database
        Document doc = this.database.get(DatabaseCollection.USER, id.getValue());
        
        // case where id doesn't exist in the database
        if (doc == null) {
        	this.logger.log("User not found for ID: " + id.getValue(), LogLevel.ERROR);
        }
        
        // convert from document -> user and return
        try {
        	return new User(
        			new UserId(doc.getObjectId("_id").toHexString()),
        			doc.getString("username"),
        			doc.getString("name"),
        			doc.getString("email"),
        			Year.valueOf(doc.getString("year").toUpperCase()),
        			doc.getList("reviewIds", String.class).stream()
        				.map(ReviewId::new)
        				.toArray(ReviewId[]::new),
        			doc.getList("commentIds", String.class).stream()
        				.map(CommentId::new)
        				.toArray(CommentId[]::new),
        			doc.getList("organizationIds", String.class).stream()
        				.map(OrganizationId::new)
        				.toArray(OrganizationId[]::new),
        			doc.getList("contactIds", String.class).stream()
        				.map(UserId::new)
        				.toArray(UserId[]::new),
        			Favorites.fromDocument(doc.get("favorites", Document.class))
        			);
        			
        } catch (Exception e) {
        	this.logger.log("Failed to parse user document: " + e.getMessage(), LogLevel.ERROR);
        	return null;
        }
    }

    @Override
    public List<User> getAll() {
        this.logger.log("Getting all users", LogLevel.INFO);
        
        List<Document> docs = this.database.list(DatabaseCollection.USER, new Document());
        List<User> users = new ArrayList<>();
        
        for (Document doc : docs)
        {
        	try {
        		User user = new User(
        				new UserId(doc.getObjectId("_id").toHexString()),
        				doc.getString("username)"),
        				doc.getString("name"),
        				doc.getString("email"),
        				Year.valueOf(doc.getString("year").toUpperCase()),
        				doc.getList("reviewIds", String.class).stream()
        					.map(ReviewId::new).toArray(ReviewId[]::new),
        				doc.getList("commentIds", String.class).stream()
        					.map(CommentId::new).toArray(CommentId[]::new),
        				doc.getList("organizationIds", String.class).stream()
        					.map(OrganizationId::new).toArray(OrganizationId[]::new),
        				doc.getList("contactIds", String.class).stream()
        					.map(UserId::new).toArray(UserId[]::new),
        				Favorites.fromDocument(doc.get("favorites", Document.class)));
        		
        		users.add(user);
        		
        	} catch (Exception e) {
        		this.logger.log("Error parsing user: " + e.getMessage(), LogLevel.ERROR);
        	}
        }
        
        return users;
    }

    @Override
    public void list(Id[] ids) {
        this.logger.log("Listing users", LogLevel.INFO);
        
        for (Id id : ids) {
        	User user = get(id);
        	
        	// check to see if ID belongs to a valid user
        	if (user != null) {
        		System.out.println("User: " + user.getUsername() + " (" + user.getEmail() + ")");
        	}
        	else {
        		System.out.println("User not found: " + id.getValue());
        	}
        }
        
        
    }
}
