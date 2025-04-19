package com.group7.lib.types.Organization;

import java.util.Objects;


import com.group7.lib.types.Ids.AnnouncementId;
import com.group7.lib.types.Ids.OrganizationId;

public class AnnouncementObject {

    private final AnnouncementId id;
    private final OrganizationId organizationId;
    private String title;
    private String content;
    private Date timeCreated;
    private Importance importance;
    private int views; 

    public EventObject(AnnouncementId id, OrganizationId organizationId, String title, String content, Date timeCreated, Importance importance, int views){
        this.id = Objects.requireNonNull(id, "Id cannot be null");
        this.organizationId = Objects.requireNonNull(organizationId, "Organization Id cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.content = Objects.requireNonNull(content, "Content cannot be null");
        this.timeCreated = Objects.requireNonNull(timeCreated, "Time created cannot be null");
        this.importance = Objects.requireNonNull(importance, "Importance cannot be null");
        this.views = Objects.requireNonNull(views, "Views cannot be null");
    }
    
    public AnnouncementId getAnnouncementId(){
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

    public void setContent(String content){
        this.content = Objects.requireNonNull(content, "Content cannot be null");
    }

    public String getContent(){
        return this.content;
    }

    public void setTimeCreated(Date timeCreated){
        this.timeCreated = Objects.requireNonNull(timeCreated, "Time Created cannot be null");
    }

    public Date getTimeCreated(){
        return this.timeCreated;
    }

    public void setImportance(Importance importance){
        this.importance = Objects.requireNonNull(importance, "Importance cannot be null");
    }

    public Importance getImportance(){
        return this.importance;
    }

    public void setViewws(int views){
        this.views = Objects.requireNonNull(views, "Views cannot be null");
    }

    public int getViews(){
        return this.views;
    }

    public Document toDocument()
    {
    	return new Document()
    			.append("id", id)
                .append("organizationId", organizationId)
                .append("title", title)
                .append("content", content)
                .append("timeCreated", timeCreated)  	
                .append("importance", importance)  	
                .append("views", views)     			
    }
    
    public static AnnouncementId fromDocument(Document doc)
    {
        
        AnnouncementId id = AnnouncementId.fromDocument(doc.get("id", Document.class));
        OrganizationId organizationId = OrganizationId.fromDocument(doc.get("organizationId", Document.class));
        String title = doc.getString("title");
        String content = doc.getString("content");
        Date timeCreated = doc.getDate("timeCreated");
        Importance importance = Importance.fromDocument(doc.get("importance", Document.class));
        int views = doc.getInteger("views");

    	return new EventObject(id, organizationId, title, content, timeCreated, importance, views);
    }

}