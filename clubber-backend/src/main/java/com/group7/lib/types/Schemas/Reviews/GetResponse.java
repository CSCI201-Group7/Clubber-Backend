package com.group7.lib.types.Schemas.Reviews;

import java.util.List;
import java.util.stream.Collectors;

import com.group7.lib.types.Review.Review;

public record GetResponse(
        String id,
        String authorId,
        String organizationId,
        String title,
        String content,
        Review.Rating rating,
        List<String> fileIds,
        String createdAt,
        String updatedAt,
        List<String> upvotes,
        List<String> downvotes,
        List<String> commentIds
        ) {

    public GetResponse(Review review) {
        this(
                review.id() != null ? review.id().toString() : null,
                review.authorId() != null ? review.authorId().toString() : null,
                review.organizationId() != null ? review.organizationId().toString() : null,
                review.title(),
                review.content(),
                review.rating(),
                review.fileIds() != null ? review.fileIds().stream().map(id -> id != null ? id.toString() : null).collect(Collectors.toList()) : null,
                review.createdAt() != null ? review.createdAt().toString() : null,
                review.updatedAt() != null ? review.updatedAt().toString() : null,
                review.upvotes() != null ? review.upvotes().stream().map(id -> id != null ? id.toString() : null).collect(Collectors.toList()) : null,
                review.downvotes() != null ? review.downvotes().stream().map(id -> id != null ? id.toString() : null).collect(Collectors.toList()) : null,
                review.commentIds() != null ? review.commentIds().stream().map(id -> id != null ? id.toString() : null).collect(Collectors.toList()) : null
        );
    }
}
