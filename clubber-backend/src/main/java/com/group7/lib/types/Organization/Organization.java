package com.group7.lib.types.Organization;

import java.util.List;

import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;
// EventId and AnnouncementId likely need creation or use String
// import com.group7.lib.types.Ids.EventId;
// import com.group7.lib.types.Ids.AnnouncementId;
// FileId likely needs creation or use String
// import com.group7.lib.types.Ids.FileId; 

public record Organization(
    OrganizationId id,
    String name,
    OrganizationType type,
    String description,
    String contactEmail,
    RecruitingStatus recruitingStatus,
    String location,
    OrganizationLinks links,
    List<UserId> memberIds,
    List<UserId> adminIds,
    List<ReviewId> reviewIds,
    String profileImageId,
    List<String> eventIds,
    List<String> announcementIds,
    String bannerImageId
) {}
