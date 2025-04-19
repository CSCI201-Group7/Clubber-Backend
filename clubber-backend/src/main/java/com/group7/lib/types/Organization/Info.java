package com.group7.lib.types.Organization;

import java.util.Objects;

public class Info {
    private int numberMembers;
    private int yearEstablished;
    private String description;
    private String meetingSchedule;
    private String location;
    private String contactEmail;
    private SocialMediaLinks socialMediaLinks;

    public Info(int numberMembers, int yearEstablished, String description, String meetingSchedule, String location, String contactEmail, SocialMediaLinks socialMediaLinks){
        this.numberMembers = Objects.requireNonNull(numberMembers, "Number of members cannot be null");
        this.yearEstablished = Objects.requireNonNull(yearEstablished, "Year established cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.meetingSchedule = Objects.requireNonNull(meetingSchedule, "Meeting Schedule cannot be null");
        this.location = Objects.requireNonNull(location, "Location cannot be null");
        this.contactEmail = Objects.requireNonNull(contactEmail, "Contact email cannot be null");
        this.socialMediaLinks = Objects.requireNonNull(socialMediaLinks, "Social media links cannot be null");
    }

    public void setNumberMembers(int numberMembers){
        this.numberMembers = Objects.requireNonNull(numberMembers, "Number of members cannot be null");
    }

    public int getNumberMembers(){
        return this.numberMembers;
    }

    public void setYearEstablished(int yearEstablished){
        this.yearEstablished = Objects.requireNonNull(yearEstablished, "Year established cannot be null");
    }

    public int getYearEstablished(){
        return this.yearEstablished;
    }

    public void setDescription(String description){
        this.description = Objects.requireNonNull(description, "Description cannot be null");
    }

    public String getDescription(){
        return this.description;
    }

    public void setMeetingSchedule(String meetingSchedule){
        this.meetingSchedule = Objects.requireNonNull(meetingSchedule, "Meeting Schedule cannot be null");
    }

    public String getMeetingSchedule(){
        return this.meetingSchedule;
    }


    public void setLocation(String location){
        this.location = Objects.requireNonNull(location, "Location cannot be null");
    }

    public String getLocation(){
        return this.location;
    }

    public void setContactEmail(String contactEmail){
        this.contactEmail = Objects.requireNonNull(contactEmail, "Contact email cannot be null");
    }

    public String getContactEmail(){
        return this.contactEmail;
    }


    public void setSocialMediaLinks(SocialMediaLinks socialMediaLinks){
        this.socialMediaLinks = Objects.requireNonNull(socialMediaLinks, "Social media links cannot be null");
    }

    public String getSocialMediaLinks(){
        return this.socialMediaLinks;
    }

    public Document toDocument()
    {
    	return new Document()
    			.append("numberMembers", numberMembers)
                .append("yearEstablished", yearEstablished)
                .append("description", description)
                .append("meetingSchedule", meetingSchedule)
                .append("location", location)    	
                .append("contactEmail", contactEmail)
                .append("socialMediaLinks", socialMediaLinks.toDocument())    	    			
    }
    
    public static Info fromDocument(Document doc)
    {
        int numberMembers = doc.getInteger("numberMembers");
        int yearEstablished = doc.getInteger("yearEstablished");
        String description = doc.getString("description");
        String meetingSchedule = doc.getString("meetingSchedule");
        String location = doc.getString("location");
        String contactEmail = doc.getString("contactEmail");
        SocialMediaLinks socialMediaLinks = SocialMediaLinks.fromDocument(doc.get("socialMediaLinks", Document.class));
    	
    	return new Info(numberMembers, yearEstablished, description, meetingSchedule, location, contactEmail, socialMediaLinks);
    }

}