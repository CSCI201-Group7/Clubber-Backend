package com.group7.lib.types.Review;

import java.time.LocalDateTime;
import java.util.List;

import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.FileId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;

public record Review(
        ReviewId id,
        UserId authorId,
        OrganizationId organizationId,
        String title,
        String content,
        Rating rating,
        List<FileId> fileIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<UserId> upvotes,
        List<UserId> downvotes,
        List<CommentId> commentIds
        ) {

    public record Rating(
            int overall,
            int community,
            int activities,
            int leadership,
            int inclusivity
            ) {

    }
}
