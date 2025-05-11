package com.group7.lib.types.Announcement;

import java.time.LocalDateTime;
import java.util.List;

import com.group7.lib.types.Ids.AnnouncementId;
import com.group7.lib.types.Ids.FileId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.UserId;

public record Announcement(
    AnnouncementId id,
    OrganizationId organizationId,
    UserId authorId,
    String title,
    String content,
    List<FileId> attachmentIds,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
