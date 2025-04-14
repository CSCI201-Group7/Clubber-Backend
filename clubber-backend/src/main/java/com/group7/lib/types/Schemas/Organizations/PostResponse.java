package com.group7.lib.types.Schemas.Organizations;

import com.group7.lib.types.Organization.Organization;

public record PostResponse(
    String id,
    String name,
    String category,
    String[] tags,
    RecruitmentInfo recruitmentInfo,
    OrganizationInfo info,
    String visibility
) {
    public PostResponse(Organization organization) {
        this(
            organization.getId().toString(),
            organization.getName(),
            organization.getCategory().toString(),
            organization.getTags(),
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
            organization.getVisibility().toString()
        );
    }
} 