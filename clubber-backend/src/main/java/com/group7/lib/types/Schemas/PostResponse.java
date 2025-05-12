package com.group7.lib.types.Schemas;

import com.group7.lib.types.Ids.base.Id;

public record PostResponse(
        String id
        ) {

    public PostResponse(Id id) {
        this(id.toString());
    }
}
