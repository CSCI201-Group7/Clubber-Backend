package com.group7.lib.types.Organization;

public class OrganizationLinks {
    private String website;
    private String linkedIn;
    private String instagram;
    private String discord;

    // Constructor
    public OrganizationLinks(String website, String linkedIn, String instagram, String discord) {
        this.website = website;
        this.linkedIn = linkedIn;
        this.instagram = instagram;
        this.discord = discord;
    }

    // Getters and Setters
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getDiscord() {
        return discord;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }
} 