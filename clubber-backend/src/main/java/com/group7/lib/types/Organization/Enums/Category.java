package com.group7.lib.types.Organization.Enums;

public enum Category {
    ACADEMIC("Academic"),
    CULTURAL("Cultural"),
    PROFESSIONAL("Professional"),
    SOCIAL("Social"),
    SPORTS("Sports");
    //TO DO: add more categories

    private final String displayCategory;

    Category(String displayCategory) {
        this.displayCategory = displayCategory;
    }

    public String getDisplayCategory() {
        return displayCategory;
    }
} 