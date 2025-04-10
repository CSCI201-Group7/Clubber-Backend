package com.group7.clubber_backend.Managers;

import java.util.List;

import com.group7.clubber_backend.Managers.base.Manager;
import com.group7.lib.types.Ids.base.Id;
import com.group7.lib.types.User.User;

public class UserManager extends Manager<User> {

    private static final UserManager instance = new UserManager();

    private UserManager() {
        super();
        // this.collection = Database.getInstance().getCollection(DatabaseCollection.USER);
    }

    // private MongoCollection<Document> collection;

    public static UserManager getInstance() {
        return instance;
    }

    @Override
    public void create(User object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public void update(User object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Id id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public User get(Id id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public List<User> getAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    public void list(Id[] ids) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'list'");
    }
}
