package com.group7.lib.types.Schemas.Announcements;

import com.group7.lib.types.Announcement.Announcement;
import com.group7.lib.types.Announcement.AnnouncementImportance;

public record GetResponse(
        String id,
        String authorId,
        String organizationId,
        String title,
        String content,
        String[] attachmentIds,
        String createdAt,
        String updatedAt,
        AnnouncementImportance importance
        ) {

    public GetResponse(Announcement announcement) {
        this(
                announcement.id().toString(),
                announcement.authorId().toString(),
                announcement.organizationId().toString(),
                announcement.title(),
                announcement.content(),
                announcement.attachmentIds().stream().map(Object::toString).toArray(String[]::new),,
                announcement.createdAt().toString(),
                announcement.updatedAt().toString(),
                announcement.importance()
        );
    }
}
