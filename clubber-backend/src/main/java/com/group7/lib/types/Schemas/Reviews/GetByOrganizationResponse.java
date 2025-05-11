package com.group7.lib.types.Schemas.Reviews;

import java.util.List;

import com.group7.lib.types.Review.Review;

// Response for getting all reviews for a specific organization
public record GetByOrganizationResponse(
    List<Review> reviews
) {} 