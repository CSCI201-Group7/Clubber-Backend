package com.group7.lib.types.Schemas.Events;

import java.util.List;
import java.util.stream.Collectors;

import com.group7.lib.types.Event.Event;
import com.group7.lib.types.Ids.FileId;

public record GetResponse(
        String id,
        String organizationId,
        String title,
        String description,
        String location,
        String startTime,
        String endTime,
        String rsvpLink,
        List<String> attachmentIds
        ) {

    public GetResponse(Event event) {
        this(
                event.id().toString(),
                event.organizationId().toString(),
                event.title(),
                event.description(),
                event.location(),
                event.startTime(),
                event.endTime(),
                event.rsvpLink(),
                event.attachmentIds().stream().map(FileId::toString)
                        .collect(Collectors.toList())
        );
    }
}
