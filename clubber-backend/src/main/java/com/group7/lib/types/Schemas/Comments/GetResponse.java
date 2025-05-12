package com.group7.lib.types.Schemas.Comments;

import java.util.Arrays;

import com.group7.lib.types.Comment.Comment;
import com.group7.lib.types.Ids.UserId;

public record GetResponse(
        String id,
        String text,
        String authorId,
        String reviewId,
        String parentCommentId,
        String createdAt,
        String[] upvotes,
        String[] downvotes
        ) {

    public GetResponse(Comment comment) {
        this(
                comment.id().toString(),
                comment.text(),
                comment.userId().toString(),
                comment.reviewId() == null ? null : comment.reviewId().toString(),
                comment.parentCommentId() == null ? null : comment.parentCommentId().toString(),
                comment.createdAt(),
                Arrays.stream(comment.upvotes()).map(UserId::toString).toArray(String[]::new),
                Arrays.stream(comment.downvotes()).map(UserId::toString).toArray(String[]::new)
        );
    }
}
