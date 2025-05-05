package com.group7.lib.types.Organization;

public class OrganizationInfo {
    private String description;
    private String contactEmail;
    private RecruitingStatus recruitingStatus;
    private String location;
    private OrganizationLinks links;

    // Constructor
    public OrganizationInfo(String description, String contactEmail, RecruitingStatus recruitingStatus, String location, OrganizationLinks links) {
        this.description = description;
        this.contactEmail = contactEmail;
        this.recruitingStatus = recruitingStatus;
        this.location = location;
        this.links = links;
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public RecruitingStatus getRecruitingStatus() {
        return recruitingStatus;
    }

    public void setRecruitingStatus(RecruitingStatus recruitingStatus) {
        this.recruitingStatus = recruitingStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public OrganizationLinks getLinks() {
        return links;
    }

    public void setLinks(OrganizationLinks links) {
        this.links = links;
    }
} 