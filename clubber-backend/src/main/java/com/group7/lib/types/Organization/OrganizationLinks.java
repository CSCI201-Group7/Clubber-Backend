package com.group7.lib.types.Organization;

import jakarta.annotation.Nullable;

public record OrganizationLinks(
    @Nullable String website,
    @Nullable String linkedIn,
    @Nullable String instagram,
    @Nullable String discord
) {} 