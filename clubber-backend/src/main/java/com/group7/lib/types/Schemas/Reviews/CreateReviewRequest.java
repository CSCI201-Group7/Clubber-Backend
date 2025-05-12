package com.group7.lib.types.Schemas.Reviews;

import java.util.List;

// Schema for POST /reviews request body
public record CreateReviewRequest(
    String organizationId, // ID of the organization being reviewed
    String title,
    String content,
    RatingDto rating, // Nested DTO for rating components
    List<String> fileIds, // List of FileId strings
    String status // e.g., "DRAFT", "PUBLISHED"
) {
    // Nested DTO for rating details
    public record RatingDto(
        int overall,
        int community,
        int activities,
        int leadership,
        int inclusivity
    ) {}
} 