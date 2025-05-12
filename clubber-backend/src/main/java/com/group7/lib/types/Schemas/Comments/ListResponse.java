package com.group7.lib.types.Schemas.Comments;

import java.util.List;
import java.util.stream.Collectors;

import com.group7.lib.types.Comment.Comment;

public record ListResponse(List<GetResponse> comments) {

    public static ListResponse fromComments(List<Comment> comments) {
        return new ListResponse(comments.stream().map(GetResponse::new).collect(Collectors.toList()));
    }
}
