package com.group7.lib.types.Review;

import java.time.LocalDateTime;
import java.util.List;

import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;

public class Review {

    private ReviewId id;
    private UserId authorId;
    private OrganizationId organizationId;
    private String title;
    private String content;
    private Rating rating;
    private List<String> fileIds; // Using String for FileId
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UserId> upvotes;
    private List<UserId> downvotes;
    private int views;
    private List<CommentId> commentIds;
    private List<String> reportIds; // Using String for ReportId
    private Status status;

    public Review(ReviewId id, UserId authorId, OrganizationId organizationId, String title, String content, Rating rating, List<String> fileIds, LocalDateTime createdAt, LocalDateTime updatedAt, List<UserId> upvotes, List<UserId> downvotes, int views, List<CommentId> commentIds, List<String> reportIds, Status status) {
        this.id = id;
        this.authorId = authorId;
        this.organizationId = organizationId;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.fileIds = fileIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.views = views;
        this.commentIds = commentIds;
        this.reportIds = reportIds;
        this.status = status;
    }
}
