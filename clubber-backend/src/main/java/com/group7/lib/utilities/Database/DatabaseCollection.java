package com.group7.lib.utilities.Database;

public enum DatabaseCollection {
    USER(DatabaseConfig.USER_COLLECTION),
    EVENT(DatabaseConfig.EVENT_COLLECTION),
    CLUB(DatabaseConfig.CLUB_COLLECTION),
    ORGANIZATION(DatabaseConfig.ORGANIZATION_COLLECTION),
    ANNOUNCEMENT(DatabaseConfig.ANNOUNCEMENT_COLLECTION),
    REVIEW(DatabaseConfig.REVIEW_COLLECTION),
    COMMENT(DatabaseConfig.COMMENT_COLLECTION),
    TEST(DatabaseConfig.TEST_COLLECTION),
    FILE(DatabaseConfig.FILE_COLLECTION),
    REPORT(DatabaseConfig.REPORT_COLLECTION);

    private final String collectionName;

    DatabaseCollection(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }
}
