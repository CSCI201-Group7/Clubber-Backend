package com.group7.lib.types.Schemas.Comments;

import com.group7.lib.types.Ids.CommentId;

public record PostResponse(
    CommentId commentId,
    String message
) {} 