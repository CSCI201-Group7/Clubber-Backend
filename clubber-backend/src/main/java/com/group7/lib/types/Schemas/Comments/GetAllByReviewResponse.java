package com.group7.lib.types.Schemas.Comments;

import java.util.List;

import com.group7.lib.types.Comment.Comment;

// Response for getting all comments for a specific review
public record GetAllByReviewResponse(
    List<Comment> comments
) {} 