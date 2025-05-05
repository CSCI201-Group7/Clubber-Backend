package com.group7.lib.types.Schemas.Organizations;

import com.group7.lib.types.Organization.OrganizationInfo;
import com.group7.lib.types.Organization.RecruitmentInfo;

public record PutRequest(
    String name,
    String category,
    String[] tags,
    RecruitmentInfo recruitmentInfo,
    OrganizationInfo info,
    String visibility
) {} 