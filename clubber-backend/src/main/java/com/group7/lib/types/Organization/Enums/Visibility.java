package com.group7.lib.types.Organization.Enums;

public enum Visibility {
    PUBLIC("Public"),
    USCONLY("USCOnly");

    private final String displayVisibility;

    Category(String displayVisibility) {
        this.displayVisibility = displayVisibility;
    }

    public String getDisplayVisibility() {
        return displayVisibility;
    }
} 