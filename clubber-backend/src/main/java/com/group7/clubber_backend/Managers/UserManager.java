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
        // TODO: Implement database creation
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public void update(User object) {
        this.logger.log("Updating user", LogLevel.INFO);
        // TODO: Implement database update
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Id id) {
        this.logger.log("Deleting user", LogLevel.INFO);
        // TODO: Implement database deletion
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public User get(Id id) {
        this.logger.log("Getting user", LogLevel.INFO);
        // TODO: Implement database retrieval
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public List<User> getAll() {
        this.logger.log("Getting all users", LogLevel.INFO);
        // TODO: Implement database retrieval of all users
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
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
        this.logger.log("Adding user interests", LogLevel.INFO);
        // TODO: Implement database update of user interests
        throw new UnsupportedOperationException("Unimplemented method 'addUserInterests'");
    }

    public List<Organization> getUserRecommendations(UserId id) {
        this.logger.log("Getting user recommendations", LogLevel.INFO);
        // TODO: Implement recommendation algorithm
        throw new UnsupportedOperationException("Unimplemented method 'getUserRecommendations'");
    }
}
