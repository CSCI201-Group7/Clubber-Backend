package com.group7.lib.types.Schemas.Organizations;

import com.group7.lib.types.Organization.Organization;
import com.group7.lib.types.Organization.OrganizationInfo;

public record PostResponse(
    String id,
    String name,
    String type,
    OrganizationInfo info,
    String visibility
) {
    public PostResponse(Organization organization) {
        this(
            organization.getId().toString(),
            organization.getName(),
            organization.getType().toString(),
            organization.getInfo(),
            ""
        );
    }
} 
