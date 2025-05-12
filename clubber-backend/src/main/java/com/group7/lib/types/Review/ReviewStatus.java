package com.group7.lib.types.Review;

public enum ReviewStatus {
    PUBLISHED("Published"),
    DRAFT("Draft"),
    HIDDEN("Hidden");

    private final String status;

    ReviewStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.status;
    }
} 