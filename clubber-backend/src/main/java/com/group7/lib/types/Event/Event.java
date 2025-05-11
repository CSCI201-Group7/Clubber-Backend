package com.group7.lib.types.Event;

import java.util.Date;
import java.util.List;

import com.group7.lib.types.Ids.EventId;
import com.group7.lib.types.Ids.FileId;
import com.group7.lib.types.Ids.OrganizationId;

public record Event(
        EventId id,
        OrganizationId organizationId,
        String title,
        String description,
        String location,
        Date startTime,
        Date endTime,
        String rsvpLink,
        List<FileId> attachmentIds
        ) {

}
