package com.group7.lib.types.Schemas.Organizations;

import com.group7.lib.types.Ids.OrganizationId;

public record PostResponse(
        String id
        ) {

    public PostResponse(OrganizationId organizationId) {
        this(organizationId.toString());
    }
}
