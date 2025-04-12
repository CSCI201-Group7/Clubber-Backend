package com.group7.clubber_backend.Managers;

import java.util.List;

import com.group7.clubber_backend.Managers.base.Manager;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.types.User.User;
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public void update(User object) {
        this.logger.log("Updating user", LogLevel.INFO);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Id id) {
        this.logger.log("Deleting user", LogLevel.INFO);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public User get(Id id) {
        this.logger.log("Getting user", LogLevel.INFO);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public List<User> getAll() {
        this.logger.log("Getting all users", LogLevel.INFO);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    public void list(Id[] ids) {
        this.logger.log("Listing users", LogLevel.INFO);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'list'");
    }
}
