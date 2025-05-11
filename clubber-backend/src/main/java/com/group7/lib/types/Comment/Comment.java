package com.group7.lib.types.Comment;

import java.time.LocalDateTime;

import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.ReviewId; // Comments are on reviews
import com.group7.lib.types.Ids.UserId;

public record Comment(
    CommentId id,
    UserId userId,
    ReviewId reviewId, // The review this comment is associated with
    String text,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 