package com.group7.lib.types.User;

public enum Year {
    FRESHMAN("Freshman"),
    SOPHOMORE("Sophomore"),
    JUNIOR("Junior"),
    SENIOR("Senior"),
    MASTER("Master"),
    DOCTOR("Doctor");

    private final String displayName;

    Year(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 