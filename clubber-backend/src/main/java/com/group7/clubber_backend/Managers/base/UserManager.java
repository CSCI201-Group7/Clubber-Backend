package com.group7.clubber_backend.Managers;

import java.util.List;
import java.util.ArrayList;

import com.group7.clubber_backend.Managers.base.Manager;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.User.User;
import com.group7.lib.types.Review.Review;
import com.group7.lib.types.Comment.Comment;
import com.group7.lib.types.Organization.Organization;
import com.group7.lib.utilities.Database.Database;
import com.group7.lib.utilities.Logger.LogLevel;
import com.group7.lib.utilities.Logger.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Component
public class UserManager extends Manager<User> {
    private final Database database;
    private final Logger logger;
    private final MongoCollection<Document> userCollection;
    private final Gson gson;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserManager(MongoClient mongoClient) {
        super();
        this.database = Database.getInstance();
        this.logger = new Logger("UserManager");
        MongoDatabase database = mongoClient.getDatabase("clubber");
        this.userCollection = database.getCollection("users");
        this.passwordEncoder = new BCryptPasswordEncoder();
        
        this.gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();
    }

    public User authenticateUser(String email, String password) {
        try {
            Bson filter = Filters.eq("email", email);
            Document doc = userCollection.find(filter).first();
            
            if(doc == null){
                return null;
            }

            String storedPassword = doc.getString("password");
            if(!passwordEncoder.matches(password, storedPassword)){
                return null;
            }

            String json = doc.toJson();
            return gson.fromJson(json, User.class);
        }
        catch(Exception e){
            logger.log("Authentication failed: " + e.getMessage(), LogLevel.ERROR);
            return null;
        }
    }

    public User createNewUser(String username, String email, String password) {
        try{
            //check if user already exists
            Bson filter = Filters.eq("email", email);
            if(userCollection.find(filter).first() != null){
                return null;
            }

            //create new user
            UserId id = new UserId(UUID.randomUUID().toString());
            String hashedPassword = passwordEncoder.encode(password);
            
            User user = new User(
                id,
                username,
                //make their name their username by default
                username, 
                email,
                Year.FRESHMAN,
                new ReviewId[0],
                new CommentId[0],
                new OrganizationId[0],
                new UserId[0],
                new Favorites()
            );

            //convert to document and add password
            String json = gson.toJson(user);
            Document doc = Document.parse(json);
            doc.append("password", hashedPassword);

            //save to database
            userCollection.insertOne(doc);
            return user;
        }
        catch(Exception e){
            logger.log("User creation failed: " + e.getMessage(), LogLevel.ERROR);
            return null;
        }
    }

    @Override
    public void create(User user){
        try{
            this.logger.log("Creating user", LogLevel.INFO);
            String json = gson.toJson(user);
            Document doc = Document.parse(json);
            userCollection.insertOne(doc);
        }
        catch(Exception e){
            logger.log("User creation failed: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public void update(User user) {
        try{
            this.logger.log("Updating user", LogLevel.INFO);
            String json = gson.toJson(user);
            Document updateDoc = Document.parse(json);
            Bson filter = Filters.eq("_id", user.getId().toString());
            Bson update = new Document("$set", updateDoc);
            userCollection.updateOne(filter, update);
        }
        catch(Exception e){
            logger.log("User update failed: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void delete(Id id){
        try{
            this.logger.log("Deleting user", LogLevel.INFO);
            Bson filter = Filters.eq("_id", id.toString());
            userCollection.deleteOne(filter);
        }
        catch(Exception e){
            logger.log("User deletion failed: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public User get(Id id){
        try{
            this.logger.log("Getting user", LogLevel.INFO);
            Bson filter = Filters.eq("_id", id.toString());
            Document doc = userCollection.find(filter).first();
            
            if(doc == null){
                return null;
            }

            String json = doc.toJson();
            return gson.fromJson(json, User.class);
        }
        catch(Exception e){
            logger.log("User retrieval failed: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Failed to get user", e);
        }
    }

    @Override
    public List<User> getAll(){
        try{
            this.logger.log("Getting all users", LogLevel.INFO);
            List<User> users = new ArrayList<>();
            for(Document doc : userCollection.find()){
                String json = doc.toJson();
                users.add(gson.fromJson(json, User.class));
            }
            return users;
        }
        catch(Exception e){
            logger.log("User retrieval failed: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Failed to get all users", e);
        }
    }

    @Override
    public void list(Id[] ids) {
        this.logger.log("Listing users", LogLevel.INFO);
        // TODO: Implement database retrieval of specific users
        throw new UnsupportedOperationException("Unimplemented method 'list'");
    }

    public List<Review> getUserReviews(UserId id) {
        this.logger.log("Getting user reviews", LogLevel.INFO);
        // TODO: Implement database retrieval of user reviews
        throw new UnsupportedOperationException("Unimplemented method 'getUserReviews'");
    }

    public List<Comment> getUserComments(UserId id) {
        this.logger.log("Getting user comments", LogLevel.INFO);
        // TODO: Implement database retrieval of user comments
        throw new UnsupportedOperationException("Unimplemented method 'getUserComments'");
    }

    public List<Organization> getUserFavorites(UserId id) {
        this.logger.log("Getting user favorites", LogLevel.INFO);
        // TODO: Implement database retrieval of user favorites
        throw new UnsupportedOperationException("Unimplemented method 'getUserFavorites'");
    }

    public void addUserInterests(UserId id, List<String> interests) {
        try{
            this.logger.log("Adding user interests", LogLevel.INFO);
            Bson filter = Filters.eq("_id", id.toString());
            Bson update = Updates.pushEach("interests", interests);
            userCollection.updateOne(filter, update);
        }
        catch(Exception e){
            logger.log("Adding user interests failed: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException("Failed to add user interests", e);
        }
    }

    public List<Organization> getUserRecommendations(UserId id) {
        this.logger.log("Getting user recommendations", LogLevel.INFO);
        // TODO: Implement recommendation algorithm
        throw new UnsupportedOperationException("Unimplemented method 'getUserRecommendations'");
    }
} 