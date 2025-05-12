package com.group7.lib.types.Review;

public record Rating(
    int overall,
    int community,
    int activities,
    int leadership,
    int inclusivity
) {}
