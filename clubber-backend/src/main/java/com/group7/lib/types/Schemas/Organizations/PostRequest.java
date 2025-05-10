package com.group7.lib.types.Schemas.Organizations;

import com.group7.lib.types.Organization.OrganizationLinks;
import com.group7.lib.types.Organization.OrganizationType;
import com.group7.lib.types.Organization.RecruitingStatus;

public record PostRequest(
    String name,
    OrganizationType type,
    String description,
    String contactEmail,
    RecruitingStatus recruitingStatus,
    String location,
    OrganizationLinks links
) {} 