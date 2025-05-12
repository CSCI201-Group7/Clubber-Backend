package com.group7.lib.types.Schemas.Announcements;

import java.util.List;
import java.util.stream.Collectors;

import com.group7.lib.types.Announcement.Announcement;

public record ListResponse(List<GetResponse> announcements) {

    public static ListResponse fromAnnouncements(List<Announcement> announcements) {
        return new ListResponse(announcements.stream().map(GetResponse::new).collect(Collectors.toList()));
    }
}
