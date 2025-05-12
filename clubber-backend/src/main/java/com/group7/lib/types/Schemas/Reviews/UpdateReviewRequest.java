package com.group7.lib.types.Schemas.Reviews;

import java.util.List;

public record UpdateReviewRequest(
        String title,
        String content,
        RatingDto rating,
        List<String> fileIds,
        String status
        ) {

    public record RatingDto(
            Integer overall,
            Integer community,
            Integer activities,
            Integer leadership,
            Integer inclusivity
            ) {

    }
}
