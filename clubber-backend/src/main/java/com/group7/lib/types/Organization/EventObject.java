package com.group7.lib.types.Organization;

import java.util.Objects;

import com.group7.lib.types.Ids.EventId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ImageId;

public class EventObject {

    private final EventId id;
    private final OrganizationId organizationId;
    private String title;
    private String description;
    private String location;
    private Date startTime;
    private Date endTime;
    private String rsvpLink;
    private ImageId image;
    private int attendees; 

    public EventObject(EventId id, OrganizationId organizationId, String title, String description, String location, Date startTime, Date endTime, String rsvpLink, ImageId image, int attendees){
        this.id = Objects.requireNonNull(id, "Id cannot be null");
        this.organizationId = Objects.requireNonNull(organizationId, "Organization Id cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.location = Objects.requireNonNull(location, "Location cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.endTime = Objects.requireNonNull(endTime, "End time cannot be null");
        this.rsvpLink = Objects.requireNonNull(rsvpLink, "RSVP link cannot be null");
        this.image = Objects.requireNonNull(image, "Image cannot be null");
        this.attendees = Objects.requireNonNull(attendees, "Attendees cannot be null");
    }
    
    public EventId getEventId(){
        return this.id;
    }

    public OrganizationId getOrganizationId(){
        return this.organizationId;
    }

    public void setTitle(String title){
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }

    public String getTitle(){
        return this.title;
    }

    public void setDescription(String description){
        this.description = Objects.requireNonNull(description, "Description cannot be null");
    }

    public String getDescription(){
        return this.description;
    }

    public void setLocation(String location){
        this.location = Objects.requireNonNull(location, "Location cannot be null");
    }

    public String getLocation(){
        return this.location;
    }

    public void setStartTime(Date startTime){
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
    }

    public Date getStartTime(){
        return this.startTime;
    }

    public void setEndTime(Date endTime){
        this.endTime = Objects.requireNonNull(endTime, "End time cannot be null");
    }

    public Date getEndTime(){
        return this.endTime;
    }

    public void setRSVPLink(String rsvpLink){
        this.rsvpLink = Objects.requireNonNull(rsvpLink, "RSVP link cannot be null");
    }

    public String getRSVPLink(){
        return this.rsvpLink;
    }

    public void setImage(ImageId image){
        this.image = Objects.requireNonNull(image, "Image cannot be null");
    }

    public ImageId getImage(){
        return this.image;
    }

    public void setAttendees(int attendees){
        this.attendees = Objects.requireNonNull(attendees, "Attendees cannot be null");
    }

    public int getAttendees(){
        return this.attendees;
    }

    public Document toDocument()
    {
    	return new Document()
    			.append("id", id)
                .append("organizationId", organizationId)
                .append("title", title)
                .append("description", description)
                .append("location", location)  	
                .append("startTime", startTime)  	
                .append("endTime", endTime)  	
                .append("rsvpLink", rsvpLink)  	
                .append("image", image)  	
                .append("attendees", attendees)     			
    }
    
    public static EventObject fromDocument(Document doc)
    {
        
        EventId id = EventId.fromDocument(doc.get("id", Document.class));
        OrganizationId organizationId = OrganizationId.fromDocument(doc.get("organizationId", Document.class));
        String title = doc.getString("title");
        String description = doc.getString("description");
        String location = doc.getString("location");
        Date startTime = doc.getDate("startTime");
        Date endTime = doc.getDate("endTime");
        String rsvpLink = doc.getString("rsvpLink");
        ImageId image = ImageId.fromDocument(doc.get("image", Document.class));
        int attendees = doc.getInteger("attendees");

    	return new EventObject(id, organizationId, title, description, location, startTime, endTime, rsvpLink, image, attendees);
    }

}