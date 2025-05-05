package com.group7.lib.types.Organization;

import java.util.Date;

public record RecruitmentInfo(
    boolean openStatus,
    String applicationLink,
    Date deadline,
    String gradeRequirements,
    String[] majorRequirements
) {} 