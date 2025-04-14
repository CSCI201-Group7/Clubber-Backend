package com.group7.lib.types.Schemas.Organizations;

import java.util.Map;

public record OrganizationInfo(
    int numberOfMembers,
    int yearOfEstablishment,
    String description,
    String meetingSchedule,
    String location,
    String contactEmail,
    Map<String, String> socialMediaLinks
) {} 