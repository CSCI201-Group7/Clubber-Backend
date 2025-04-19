package com.group7.lib.types.Schemas.Organization;

import org.springframework.web.bind.annotation.ResponseBody;

import com.group7.lib.types.Organization.Organization;

@ResponseBody
public record GetResponse(
        String name, 
        OrganizationId id, 
        Category category, 
        String[] tags, 
        UserId[] memberIds, 
        UserId[] adminIds, 
        double rating, 
        ReviewId[] reviewIds, 
        RecruitmentInfo recruitmentInfo, 
        Info info, 
        EventObject[] events, 
        AnnouncementObject[] announcements, 
        ImageId logo, 
        ImageId banner, 
        Visibility visibility) {

    public GetResponse(Organization organization) {
        this(
                organization.getName(),
                organization.getId().toString(),
                organization.getCategory(),
                convertToStringArray(organization.getTags()),
                convertToStringArray(organization.getMemberIds()),
                convertToStringArray(organization.getAdminIds()),
                organization.getRating(),
                convertToStringArray(organization.getReviewIds()),
                organization.getRecruitmentInfo(),
                organization.getInfo();
                convertToStringArray(organization.getEvents()),
                convertToStringArray(organization.getAnnouncements()),
                organization.getLogo(),
                organization.getBanner(),
                organization.getVisibility(),
        );
    }

    private static String[] convertToStringArray(Object[] ids) {
        if (ids == null) {
            return new String[0];
        }
        String[] result = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            result[i] = ids[i].toString();
        }
        return result;
    }
}
