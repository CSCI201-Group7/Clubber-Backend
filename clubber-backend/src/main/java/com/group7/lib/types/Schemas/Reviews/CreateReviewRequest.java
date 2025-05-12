package com.group7.lib.types.Schemas.Reviews;

import java.util.List;

public record CreateReviewRequest(
        String organizationId,
        String title,
        String content,
        RatingDto rating,
        List<String> fileIds,
        String status
        ) {

    public record RatingDto(
            int overall,
            int community,
            int activities,
            int leadership,
            int inclusivity
            ) {

    }
}
