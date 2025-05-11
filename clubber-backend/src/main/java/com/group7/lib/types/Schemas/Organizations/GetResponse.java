package com.group7.lib.types.Schemas.Organizations;

import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;

import com.group7.lib.types.Organization.Organization;
import com.group7.lib.types.Organization.OrganizationLinks;
import com.group7.lib.types.Organization.OrganizationType;
import com.group7.lib.types.Organization.RecruitingStatus;

@ResponseBody
public record GetResponse(
    String id,
    String name,
    OrganizationType type,
    String description,
    String contactEmail,
    RecruitingStatus recruitingStatus,
    String location,
    OrganizationLinks links,
    String[] memberIds,
    String[] adminIds,
    String[] reviewIds,
    String profileImageId,
    String[] eventIds,
    String[] announcementIds
) {
    public GetResponse(Organization organization) {
        this(
            organization.id().toString(),
            organization.name(),
            organization.type(),
            organization.description(),
            organization.contactEmail(),
            organization.recruitingStatus(),
            organization.location(),
            organization.links(),
            convertToStringArray(organization.memberIds()),
            convertToStringArray(organization.adminIds()),
            convertToStringArray(organization.reviewIds()),
            organization.profileImageId(),
            convertToStringArray(organization.eventIds()),
            convertToStringArray(organization.announcementIds())
        );
    }

    private static String[] convertToStringArray(List<?> ids) {
        if (ids == null) {
            return new String[0];
        }
        String[] result = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            result[i] = ids.get(i).toString();
        }
        return result;
    }
} 