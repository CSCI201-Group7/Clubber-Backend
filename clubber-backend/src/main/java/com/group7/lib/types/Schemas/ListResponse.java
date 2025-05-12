package com.group7.lib.types.Schemas;

import java.util.List;

public class ListResponse<T> {
    private final List<T> items;

    public ListResponse(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public static <T> ListResponse<T> fromList(List<T> items) {
        return new ListResponse<>(items);
    }
}
