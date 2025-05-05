package com.group7.lib.types.Announcement;

import java.time.LocalDateTime;

import com.group7.lib.types.Ids.OrganizationId;
// AnnouncementId likely needs creation or use String
// import com.group7.lib.types.Ids.AnnouncementId;

public class Announcement {
    private String id; // Using String for AnnouncementId
    private OrganizationId organizationId;
    private String title;
    private String content;
    private LocalDateTime timeCreated;
    private AnnouncementImportance importance;
    private int views;

    // Constructor
    public Announcement(String id, OrganizationId organizationId, String title, String content,
                        LocalDateTime timeCreated, AnnouncementImportance importance, int views) {
        this.id = id;
        this.organizationId = organizationId;
        this.title = title;
        this.content = content;
        this.timeCreated = timeCreated;
        this.importance = importance;
        this.views = views;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(OrganizationId organizationId) {
        this.organizationId = organizationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public AnnouncementImportance getImportance() {
        return importance;
    }

    public void setImportance(AnnouncementImportance importance) {
        this.importance = importance;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
} 