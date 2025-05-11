package com.group7.lib.types.Schemas.Organizations;

import java.util.List;

import com.group7.lib.types.Organization.Organization;

public record GetAllResponse(List<Organization> organizations) {

}
