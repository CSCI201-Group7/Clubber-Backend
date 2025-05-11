package com.group7.lib.types.Schemas.Comments;

import com.group7.lib.types.Comment.Comment;

public record PutResponse(
    Comment updatedComment,
    String message
) {} 