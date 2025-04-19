package com.group7.lib.types.Organization;

import java.util.Arrays;
import java.util.Objects;

public class RecruitmentInfo {

    private boolean openStatus;
    private String application;
    private Date deadline;
    private String gradeRequirements;
    private String[] majorRequirements;

    public Info(boolean openStatus, String application, Date deadline, String gradeRequirements, String[] majorRequirements){
        this.openStatus = Objects.requireNonNull(openStatus, "Open Status cannot be null");
        this.application = Objects.requireNonNull(application, "Application cannot be null");
        this.deadline = Objects.requireNonNull(deadline, "Deadline members cannot be null");
        this.gradeRequirements = Objects.requireNonNull(gradeRequirements, "Grade Requirements cannot be null");
        this.majorRequirements = (majorRequirements != null) ? Arrays.copyOf(majorRequirements, majorRequirements.length) : new String[0];
    }

    public void setOpenStatus(boolean openStatus){
        this.openStatus = Objects.requireNonNull(openStatus, "Open Status cannot be null");
    }

    public boolean getOpenStatus(){
        return this.openStatus;
    }

    public void setApplication(String application){
        this.application = Objects.requireNonNull(application, "Application cannot be null");
    }

    public String getApplication(){
        return this.application;
    }

    public void setDeadline(Date deadline){
        this.deadline = Objects.requireNonNull(deadline, "Deadline members cannot be null");
    }

    public Date getDeadline(){
        return this.deadline;
    }

    public void setGradeRequirements(String gradeRequirements){
        this.gradeRequirements = Objects.requireNonNull(gradeRequirements, "Grade Requirements cannot be null");
    }

    public String getGradeRequirements(){
        return this.gradeRequirements;
    }

    public void setMajorRequirements(String[] majorRequirements){
        this.majorRequirements = (majorRequirements != null) ? Arrays.copyOf(majorRequirements, majorRequirements.length) : new String[0];
     }

    public String getMajorRequirements(){
        return Arrays.copyOf(majorRequirements, majorRequirements.length);
    }

    public Document toDocument()
    {
    	return new Document()
    			.append("openStatus", openStatus)
                .append("application", application)
                .append("deadline", deadline)
                .append("gradeRequirements", gradeRequirements)
                .append("majorRequirements", majorRequirements)  	    			
    }
    
    public static RecruitmentInfo fromDocument(Document doc)
    {
        
        boolean openStatus = doc.getBoolean("openStatus");
        String application = doc.getString("application");
        Date deadline = doc.getDate("deadline");
        String gradeRequirements = doc.getString("gradeRequirements");
        String[] majorRequirements = doc.getList("majorRequirements", String.class).stream()
    							.map(String::new)
    							.toArray(String[]::new);
    	return new RecruitmentInfo(openStatus, application, deadline, gradeRequirements, majorRequirements);
    }

}