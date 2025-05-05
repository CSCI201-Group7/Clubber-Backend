package com.group7.lib.types.Report;

import java.time.LocalDateTime;

import com.group7.lib.types.Ids.ReportId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Ids.base.Id; // For contentId (ReviewId | CommentId)

public class Report {
    private ReportId id;
    private UserId reporterId;
    private Id contentId; // Represents ReviewId or CommentId
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime timeCreated;

    // Constructor
    public Report(ReportId id, UserId reporterId, Id contentId, ReportReason reason, String description, ReportStatus status, LocalDateTime timeCreated) {
        this.id = id;
        this.reporterId = reporterId;
        this.contentId = contentId;
        this.reason = reason;
        this.description = description;
        this.status = status;
        this.timeCreated = timeCreated;
    }

    // Getters and Setters (or use Lombok)
    public ReportId getId() {
        return id;
    }

    public void setId(ReportId id) {
        this.id = id;
    }

    public UserId getReporterId() {
        return reporterId;
    }

    public void setReporterId(UserId reporterId) {
        this.reporterId = reporterId;
    }

    public Id getContentId() {
        return contentId;
    }

    public void setContentId(Id contentId) {
        this.contentId = contentId;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }
} 