package com.group7.lib.types.Schemas.Announcements;

import java.util.List;
import java.util.stream.Collectors;

import com.group7.lib.types.Announcement.Announcement;

public record GetByOrgResponse(List<GetResponse> announcements) {

    /**
     * Creates a GetByOrgResponse from a list of Announcement objects. This
     * factory method handles the conversion from {@code Announcement} domain
     * objects to {@code GetResponse} schema objects.
     *
     * @param announcementList The list of {@code Announcement} objects.
     * @return A new {@code GetByOrgResponse} instance.
     */
    public static GetByOrgResponse fromAnnouncements(List<Announcement> announcementList) {
        if (announcementList == null) {
            // Return a response with an empty list for null input.
            // This is generally safer than returning null or throwing an exception,
            // unless a different behavior is explicitly required.
            return new GetByOrgResponse(java.util.Collections.emptyList());
        }
        List<GetResponse> getResponseList = announcementList.stream()
                .map(GetResponse::new) // Assumes GetResponse has a constructor: GetResponse(Announcement announcement)
                .collect(Collectors.toList());
        return new GetByOrgResponse(getResponseList);
    }
}
