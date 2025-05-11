package com.group7.lib.types.Organization;

public enum OrganizationType {
    Academic("Academic"),
    Cultural("Cultural"),
    Sports("Sports"),
    Religious("Religious"),
    Political("Political"),
    Social("Social"),
    Recreational("Recreational"),
    Professional("Professional"),
    Hobby("Hobby"),
    Other("Other");

    private final String value;

    OrganizationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
