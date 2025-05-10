package com.group7.lib.types.Announcement;

import java.time.LocalDateTime;

import com.group7.lib.types.Ids.OrganizationId;
// AnnouncementId likely needs creation or use String
// import com.group7.lib.types.Ids.AnnouncementId;

public record Announcement(
    String id,
    OrganizationId organizationId,
    String title,
    String content,
    LocalDateTime timeCreated,
    AnnouncementImportance importance,
    int views
) {} 