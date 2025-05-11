package com.group7.lib.types.Schemas.Reviews;

import java.util.List;

import com.group7.lib.types.Review.Review;

public record GetAllResponse(
    List<Review> reviews
) {} 