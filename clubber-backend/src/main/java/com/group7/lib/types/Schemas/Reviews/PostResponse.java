package com.group7.lib.types.Schemas.Reviews;

import com.group7.lib.types.Ids.ReviewId;

public record PostResponse(
        String reviewId
        ) {

    public PostResponse(ReviewId reviewId) {
        this(reviewId.toString());
    }
}
