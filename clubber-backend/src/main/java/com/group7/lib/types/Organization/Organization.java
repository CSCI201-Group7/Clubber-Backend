package com.group7.lib.types.Organization;

import java.util.Arrays;
import java.util.Objects;

import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.ImageID;

public class Organization {
    private String name;
    private final OrganizationId id;
    private Category category;
    private String[] tags;
    private UserId[] memberIds;
    private UserId[] adminIds;
    private double rating;
    private ReviewId[] reviewIds;
    private RecruitmentInfo recruitmentInfo;
    private Info info;
    private EventObject[] events;
    private AnnouncementObject[] announcements;
    private ImageId logo;
    private ImageId banner;
    private Visibility visibility;

    public Organization(String name, OrganizationId id, Category category, String[] tags, UserId[] memberIds, UserId[] adminIds, double rating, ReviewId[] reviewIds, RecruitmentInfo recruitmentInfo, Info info, EventObject[] events, AnnouncementObject[] announcements, ImageId logo, ImageId banner, Visibility visibility){
        this.name = Objects.requireNonNull(name, "Organization name cannot be null");
        this.id = Objects.requireNonNull(id, "Organization ID cannot be null");
        this.category = Objects.requireNonNull(category, "Organization category cannot be null");
        this.tags = (tags != null) ? Arrays.copyOf(tags, tags.length) : new String[0];
        this.memberIds = (memberIds != null) ? Arrays.copyOf(memberIds, memberIds.length) : new UserId[0];
        this.adminIds = (adminIds != null) ? Arrays.copyOf(adminIds, adminIds.length) : new UserId[0];
        this.rating = Objects.requireNonNull(rating, "Organization rating cannot be null");
        this.reviewIds = (reviewIds != null) ? Arrays.copyOf(reviewIds, reviewIds.length) : new ReviewId[0];
        this.recruitmentInfo = Objects.requireNonNull(recruitmentInfo, "Organization recruitment info cannot be null");
        this.info = Objects.requireNonNull(info, "Organization info cannot be null");
        this.events = (events != null) ? Arrays.copyOf(events, events.length) : new EventObject[0];
        this.announcements = (announcements != null) ? Arrays.copyOf(announcements, announcements.length) : new AnnouncementObject[0];
        this.logo = Objects.requireNonNull(logo, "Organization logo cannot be null");
        this.banner = Objects.requireNonNull(banner, "Organization banner cannot be null");
        this.visibility = Objects.requireNonNull(visibility, "Organization visibility cannot be null");
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = Objects.requireNonNull(name, "Organization name cannot be null");
    }

    public void getId(){
        return this.id;
    }

    public void setCategory(Category category){
        this.category = Objects.requireNonNull(category, "Organization category cannot be null");
    }

    public Category getCategory(){
        return this.category;
    }

    public void setTags(String[] tags){
        this.tags = (tags != null) ? Arrays.copyOf(tags, tags.length) : new String[0];
    }

    public String[] getTags(){
        return Arrays.copyOf(tags, tags.length);
    }

    public void setMemberIds(UserId[] memberIds){
        this.memberIds = (memberIds != null) ? Arrays.copyOf(memberIds, memberIds.length) : new UserId[0];
    }

    public UserId[] getMemberIds(){
        return Arrays.copyOf(memberIds, memberIds.length);
    }

    public void setAdminIds(UserId[] adminIds){
        this.adminIds = (adminIds != null) ? Arrays.copyOf(adminIds, adminIds.length) : new UserId[0];
    }

    public UserId[] getAdminIds(){
        return Arrays.copyOf(adminIds, adminIds.length);
    }

    public void setRating(double rating){
        this.rating = Objects.requireNonNull(rating, "Organization rating cannot be null");
    }

    public double getRating(){
        return this.rating;
    }

    public void setReviewIds(ReviewId[] reviewIds){
        this.reviewIds = (reviewIds != null) ? Arrays.copyOf(reviewIds, reviewIds.length) : new ReviewId[0];
    }

    public ReviewId[] getReviewIds(){
        return Arrays.copyOf(reviewIds, reviewIds.length);
    }

    public void setRecruitmentInfo(RecruitmentInfo recruitment){
        this.recruitment = Objects.requireNonNull(recruitmentInfo, "Organization recruitment info cannot be null");
    }

    public RecruitmentInfo getRecruitmentInfo(){
        return this.recruitment;
    }

    public void setInfo(Info info){
        this.info = Objects.requireNonNull(info, "Organization info cannot be null");
    }

    public Info getInfo(){
        return this.info;
    }

    public void setEvents(EventObject[] events){
        this.events = (events != null) ? Arrays.copyOf(events, events.length) : new EventObject[0];
    }

    public EventObject[] getEvents(){
        return Arrays.copyOf(events, events.length);
    }

    public void setAnnouncements(AnnouncementObject[] announcements){
        this.announcements = (announcements != null) ? Arrays.copyOf(announcements, announcements.length) : new AnnouncementObject[0];
    }

    public AnnouncementObject[] getAnnouncements(){
        return Arrays.copyOf(announcements, announcements.length);
    }

    public void setLogo(ImageId logo){
        this.logo = Objects.requireNonNull(logo, "Organization logo cannot be null");
    }

    public ImageId getLogo(){
        return this.logo;
    }

    public void setBanner(ImageId banner){
        this.banner = Objects.requireNonNull(banner, "Organization banner cannot be null");
    }

    public ImageId getBanner(){
        return this.banner
    }

    public void setVisibility(Visibility visibility){
        this.visibility =  Objects.requireNonNull(visibility, "Organization visibility cannot be null");
    }

    public Visibility getVisibility(){
        return this.visibility;
    }

}
