package com.group7.lib.types.Schemas.Announcements;

import java.util.List;

import com.group7.lib.types.Announcement.Announcement;

public record GetByOrgResponse(List<Announcement> announcements) {} 