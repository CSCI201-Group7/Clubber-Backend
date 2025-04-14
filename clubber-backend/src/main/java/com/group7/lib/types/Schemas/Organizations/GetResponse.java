package com.group7.lib.types.Schemas.Organizations;

import org.springframework.web.bind.annotation.ResponseBody;
import com.group7.lib.types.Organization.Organization;
import java.util.Date;

@ResponseBody
public record GetResponse(
    String id,
    String name,
    String category,
    String[] tags,
    String[] memberIds,
    String[] adminIds,
    double rating,
    String[] reviewIds,
    RecruitmentInfo recruitmentInfo,
    OrganizationInfo info,
    Event[] events,
    Announcement[] announcements,
    String logoId,
    String bannerId,
    String visibility
) {
    public GetResponse(Organization organization) {
        this(
            organization.getId().toString(),
            organization.getName(),
            organization.getCategory().toString(),
            organization.getTags(),
            convertToStringArray(organization.getMemberIds()),
            convertToStringArray(organization.getAdminIds()),
            organization.getRating(),
            convertToStringArray(organization.getReviewIds()),
            new RecruitmentInfo(
                organization.getRecruitmentInfo().isOpenStatus(),
                organization.getRecruitmentInfo().getApplicationLink(),
                organization.getRecruitmentInfo().getDeadline(),
                organization.getRecruitmentInfo().getGradeRequirements(),
                organization.getRecruitmentInfo().getMajorRequirements()
            ),
            new OrganizationInfo(
                organization.getInfo().getNumberOfMembers(),
                organization.getInfo().getYearOfEstablishment(),
                organization.getInfo().getDescription(),
                organization.getInfo().getMeetingSchedule(),
                organization.getInfo().getLocation(),
                organization.getInfo().getContactEmail(),
                organization.getInfo().getSocialMediaLinks()
            ),
            organization.getEvents(),
            organization.getAnnouncements(),
            organization.getLogoId().toString(),
            organization.getBannerId().toString(),
            organization.getVisibility().toString()
        );
    }

    private static String[] convertToStringArray(Object[] ids) {
        if(ids == null){
            return new String[0];
        }
        String[] result = new String[ids.length];

        for(int i = 0; i < ids.length; i++){
            result[i] = ids[i].toString();
        }
        return result;
    }
} 