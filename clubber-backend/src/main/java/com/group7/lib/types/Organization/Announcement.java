package com.group7.lib.types.Organization;

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