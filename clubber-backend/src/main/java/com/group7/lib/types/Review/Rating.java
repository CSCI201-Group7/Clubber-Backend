package com.group7.lib.types.Review;

public class Rating {

    private final double overall;
    private final double community;
    private final double activities;
    private final double leadership;
    private final double inclusivity;

    public Rating(double overall, double community, double activities, double leadership, double inclusivity) {
        this.overall = overall;
        this.community = community;
        this.activities = activities;
        this.leadership = leadership;
        this.inclusivity = inclusivity;
    }

    public double getOverall() {
        return overall;
    }

    public double getCommunity() {
        return community;
    }

    public double getActivities() {
        return activities;
    }

    public double getLeadership() {
        return leadership;
    }

    public double getInclusivity() {
        return inclusivity;
    }
}
