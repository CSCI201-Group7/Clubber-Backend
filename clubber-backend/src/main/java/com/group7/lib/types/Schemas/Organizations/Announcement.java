package com.group7.lib.types.Schemas.Organizations;

import java.util.Date;

public record Announcement(
    String id,
    String organizationId,
    String title,
    String content,
    Date timeCreated,
    String importance,
    int views
) {} 