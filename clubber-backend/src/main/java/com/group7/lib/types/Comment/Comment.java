package com.group7.lib.types.Comment;

import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.ReviewId; // Comments are on reviews
import com.group7.lib.types.Ids.UserId;

public record Comment(
        CommentId id,
        UserId userId,
        CommentId parentCommentId, // parent comment
        ReviewId reviewId, // parent review
        String text,
        String createdAt,
        UserId[] upvotes,
        UserId[] downvotes
        ) {

}
