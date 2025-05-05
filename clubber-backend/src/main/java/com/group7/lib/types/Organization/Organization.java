package com.group7.lib.types.Organization;

import java.util.List;

import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;
// EventId and AnnouncementId likely need creation or use String
// import com.group7.lib.types.Ids.EventId;
// import com.group7.lib.types.Ids.AnnouncementId;
// FileId likely needs creation or use String
// import com.group7.lib.types.Ids.FileId; 

public class Organization {
    private OrganizationId id;
    private String name;
    private OrganizationType type;
    private OrganizationInfo info;
    private List<UserId> memberIds;
    private List<UserId> adminIds;
    private List<ReviewId> reviewIds;
    private String profileImageId; // Using String for FileId
    private List<String> eventIds; // Using String for EventId
    private List<String> announcementIds; // Using String for AnnouncementId
    private String bannerImageId; // Using String for FileId

    // Constructor
    public Organization(OrganizationId id, String name, OrganizationType type, OrganizationInfo info,
                        List<UserId> memberIds, List<UserId> adminIds, List<ReviewId> reviewIds,
                        String profileImageId, List<String> eventIds, List<String> announcementIds,
                        String bannerImageId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.info = info;
        this.memberIds = memberIds;
        this.adminIds = adminIds;
        this.reviewIds = reviewIds;
        this.profileImageId = profileImageId;
        this.eventIds = eventIds;
        this.announcementIds = announcementIds;
        this.bannerImageId = bannerImageId;
    }

    // Getters and Setters
    public OrganizationId getId() {
        return id;
    }

    public void setId(OrganizationId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public OrganizationInfo getInfo() {
        return info;
    }

    public void setInfo(OrganizationInfo info) {
        this.info = info;
    }

    public List<UserId> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<UserId> memberIds) {
        this.memberIds = memberIds;
    }

    public List<UserId> getAdminIds() {
        return adminIds;
    }

    public void setAdminIds(List<UserId> adminIds) {
        this.adminIds = adminIds;
    }

    public List<ReviewId> getReviewIds() {
        return reviewIds;
    }

    public void setReviewIds(List<ReviewId> reviewIds) {
        this.reviewIds = reviewIds;
    }

    public String getProfileImageId() {
        return profileImageId;
    }

    public void setProfileImageId(String profileImageId) {
        this.profileImageId = profileImageId;
    }

    public List<String> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<String> eventIds) {
        this.eventIds = eventIds;
    }

    public List<String> getAnnouncementIds() {
        return announcementIds;
    }

    public void setAnnouncementIds(List<String> announcementIds) {
        this.announcementIds = announcementIds;
    }

    public String getBannerImageId() {
        return bannerImageId;
    }

    public void setBannerImageId(String bannerImageId) {
        this.bannerImageId = bannerImageId;
    }
}
