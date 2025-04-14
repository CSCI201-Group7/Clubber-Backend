package com.group7.lib.types.Schemas.Organizations;

import java.util.Date;

public record Event(
    String id,
    String organizationId,
    String title,
    String description,
    String location,
    Date startTime,
    Date endTime,
    String rsvpLink,
    String imageId,
    int attendees
) {} 