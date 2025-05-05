package com.group7.lib.types.Schemas.Organizations;

import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;

import com.group7.lib.types.Organization.Organization;
import com.group7.lib.types.Organization.OrganizationInfo;

@ResponseBody
public record GetResponse(
    String id,
    String name,
    String type,
    String[] memberIds,
    String[] adminIds,
    String[] reviewIds,
    OrganizationInfo info,
    String[] eventIds,
    String[] announcementIds,
    String logoId,
    String bannerId,
    String visibility
) {
    public GetResponse(Organization organization) {
        this(
            organization.getId().toString(),
            organization.getName(),
            organization.getType().toString(),
            convertToStringArray(organization.getMemberIds()),
            convertToStringArray(organization.getAdminIds()),
            convertToStringArray(organization.getReviewIds()),
            organization.getInfo(),
            convertToStringArray(organization.getEventIds()),
            convertToStringArray(organization.getAnnouncementIds()),
            organization.getProfileImageId(),
            organization.getBannerImageId(),
            "VISIBLE"
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