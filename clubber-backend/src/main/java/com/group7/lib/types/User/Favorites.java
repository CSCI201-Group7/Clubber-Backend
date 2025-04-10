package com.group7.lib.types.User;

import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId;

public class Favorites {
    private final ReviewId[] reviewIds;
    private final OrganizationId[] organizationIds;

    public Favorites(ReviewId[] reviewIds, OrganizationId[] organizationIds) {
        this.reviewIds = reviewIds;
        this.organizationIds = organizationIds;
    }

    public ReviewId[] getReviewIds() {
        return reviewIds;
    }

    public OrganizationId[] getOrganizationIds() {
        return organizationIds;
    }
} 