package com.group7.lib.types.Organization;

public enum RecruitingStatus {
    Open("Open"),
    Closed("Closed"),
    Unknown("Unknown");

    private final String value;

    RecruitingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
