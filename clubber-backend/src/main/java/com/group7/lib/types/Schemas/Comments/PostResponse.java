package com.group7.lib.types.Schemas.Comments;

import com.group7.lib.types.Ids.CommentId;

public record PostResponse(
        String commentId
        ) {

    public PostResponse(CommentId commentId) {
        this(commentId.toString());
    }
}
