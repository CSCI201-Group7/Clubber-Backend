package com.group7.lib.types.Schemas.Announcements;

import com.group7.lib.types.Ids.AnnouncementId;

public record PostResponse(String id) {

    public PostResponse(AnnouncementId announcementId) {
        this(announcementId.toString());
    }
}
