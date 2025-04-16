package com.group7.lib.types.User;

import java.util.Arrays;

import org.bson.Document;

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
    
    public Document toDocument()
    {
    	return new Document()
    			.append("reviewIds", Arrays.stream(reviewIds)
    					.map(ReviewId::getValue)
    					.toList())
    			.append("organizationIds", Arrays.stream(organizationIds)
    					.map(OrganizationId::getValue)
    					.toList());
    }
    
    public static Favorites fromDocument(Document doc)
    {
    	
    	ReviewId[] reviewIds = doc.getList("reviewIds", String.class).stream()
    							.map(ReviewId::new)
    							.toArray(ReviewId[]::new);
    	
    	OrganizationId[] organizationIds = doc.getList("organizationIds", String.class).stream()
    							.map(OrganizationId::new)
    							.toArray(OrganizationId[]::new);
    	
    	return new Favorites(reviewIds, organizationIds);
    }
} 