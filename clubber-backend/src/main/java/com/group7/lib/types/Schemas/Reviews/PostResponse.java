package com.group7.lib.types.Schemas.Reviews;

import com.group7.lib.types.Ids.ReviewId;

public record PostResponse(
    ReviewId reviewId,
    String message
) {} 