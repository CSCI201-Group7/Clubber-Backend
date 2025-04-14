package com.group7.lib.types.Schemas.Organizations;

public record PostRequest(
    String name,
    String category,
    String[] tags,
    RecruitmentInfo recruitmentInfo,
    OrganizationInfo info,
    String visibility
) {} 