package com.group7.lib.types.Review;

import java.time.LocalDateTime;

import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;

public record Review(
    ReviewId id,
    UserId userId,
    OrganizationId organizationId, // The organization being reviewed
    int rating, // e.g., 1 to 5 stars
    String text,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
