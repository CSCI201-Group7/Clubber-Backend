package com.group7.clubber_backend.Managers.base;

import java.util.List;

import com.group7.lib.types.Ids.base.Id;

public abstract class Manager<T> {

    public abstract Id create(T object);

    public abstract void update(T object);

    public abstract void delete(Id id);

    public abstract T get(Id id);

    public abstract List<T> getAll();

    public abstract List<T> list(Id[] ids);

    public abstract List<T> search(String query);
}
