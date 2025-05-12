package com.group7.lib.types.Schemas.Reviews;

import java.util.List;

// Schema for PUT /reviews/{reviewId} request body
// All fields are optional for an update.
public record UpdateReviewRequest(
    String title, // Optional: new title
    String content, // Optional: new content
    RatingDto rating, // Optional: new rating scores
    List<String> fileIds, // Optional: new list of file IDs
    String status // Optional: new status, e.g., "DRAFT", "PUBLISHED", "HIDDEN"
) {
    // Nested DTO for rating details (can be null if not updating rating)
    public record RatingDto(
        Integer overall, // Use Integer for nullability
        Integer community,
        Integer activities,
        Integer leadership,
        Integer inclusivity
    ) {}
} 