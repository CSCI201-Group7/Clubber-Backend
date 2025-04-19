package com.group7.lib.types.Organization.Enums;

public enum Importance {
    NORMAL("Normal"),
    IMPORTANT("Important");

    private final String displayImportance;

    Category(String displayImportance) {
        this.displayImportance = displayImportance;
    }

    public String getDisplayImportance() {
        return displayImportance;
    }
} 