package com.group7.lib.types.Comment;

import java.time.LocalDateTime;
import java.util.List;

import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.ReportId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Ids.base.Id; // For parentId (ReviewId | CommentId)

public class Comment {
    private CommentId id;
    private UserId authorId;
    private CommentType type;
    private Id parentId; // Represents ReviewId or CommentId
    private String content;
    private LocalDateTime timeCreated;
    private LocalDateTime timeUpdated;
    private List<UserId> upvotes;
    private List<UserId> downvotes;
    private boolean flagged;
    private CommentStatus status;
    private List<ReportId> reportIds;
    private boolean isClubResponse;

    // Constructor
    public Comment(CommentId id, UserId authorId, CommentType type, Id parentId, String content,
                   LocalDateTime timeCreated, LocalDateTime timeUpdated, List<UserId> upvotes,
                   List<UserId> downvotes, boolean flagged, CommentStatus status,
                   List<ReportId> reportIds, boolean isClubResponse) {
        this.id = id;
        this.authorId = authorId;
        this.type = type;
        this.parentId = parentId;
        this.content = content;
        this.timeCreated = timeCreated;
        this.timeUpdated = timeUpdated;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.flagged = flagged;
        this.status = status;
        this.reportIds = reportIds;
        this.isClubResponse = isClubResponse;
    }

    // Getters and Setters
    public CommentId getId() {
        return id;
    }

    public void setId(CommentId id) {
        this.id = id;
    }

    public UserId getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UserId authorId) {
        this.authorId = authorId;
    }

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    public Id getParentId() {
        return parentId;
    }

    public void setParentId(Id parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public LocalDateTime getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(LocalDateTime timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public List<UserId> getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(List<UserId> upvotes) {
        this.upvotes = upvotes;
    }

    public List<UserId> getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(List<UserId> downvotes) {
        this.downvotes = downvotes;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public CommentStatus getStatus() {
        return status;
    }

    public void setStatus(CommentStatus status) {
        this.status = status;
    }

    public List<ReportId> getReportIds() {
        return reportIds;
    }

    public void setReportIds(List<ReportId> reportIds) {
        this.reportIds = reportIds;
    }

    public boolean isClubResponse() {
        return isClubResponse;
    }

    public void setClubResponse(boolean clubResponse) {
        isClubResponse = clubResponse;
    }
} 