package com.group7.lib.types.Schemas.Organizations;

import com.group7.lib.types.Organization.OrganizationLinks;
import com.group7.lib.types.Organization.OrganizationType;
import org.springframework.web.multipart.MultipartFile;

public record PostRequest(
    String name,
    OrganizationType type,
    String description,
    String contactEmail,
    String location,
    OrganizationLinks links,
    MultipartFile profileImage
) {}
