package com.group7.lib.types.Schemas.Organizations;

import java.util.List;
import java.util.stream.Collectors;

import com.group7.lib.types.Organization.Organization;

public record GetAllResponse(List<GetResponse> organizations) {

    public static GetAllResponse fromOrganizations(List<Organization> inputOrganizations) {
        List<GetResponse> transformedOrganizations = inputOrganizations.stream()
                                                                     .map(GetResponse::new)
                                                                     .collect(Collectors.toList());
        return new GetAllResponse(transformedOrganizations);
    }
}
