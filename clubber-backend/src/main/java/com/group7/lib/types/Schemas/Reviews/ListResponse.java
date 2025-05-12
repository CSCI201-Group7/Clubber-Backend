package com.group7.lib.types.Schemas.Reviews;

import java.util.List;
import java.util.stream.Collectors;

import com.group7.lib.types.Review.Review;

public record ListResponse(List<GetResponse> reviews) {

    public static ListResponse fromReviews(List<Review> reviews) {
        return new ListResponse(reviews.stream().map(GetResponse::new).collect(Collectors.toList()));
    }
}
