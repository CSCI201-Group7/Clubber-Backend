package com.group7.lib.types.Schemas.Comments;

import com.group7.lib.types.Comment.Comment;

public record GetResponse(
        String id,
        String text,
        String authorId,
        String reviewId,
        String createdAt,
        String updatedAt
        ) {

    public GetResponse(Comment comment) {
        this(
                comment.id().toString(),
                comment.text(),
                comment.userId().toString(),
                comment.reviewId().toString(),
                comment.createdAt().toString(),
                comment.updatedAt().toString()
        );
    }
}
