package com.group7.lib.types.Schemas.Comments;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record PostRequest(
        @Nullable
        String reviewId,
        @Nullable
        String parentCommentId,
        @Nonnull
        String text
        ) {

}
