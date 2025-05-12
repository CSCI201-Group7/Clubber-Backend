package com.group7.lib.types.User;

import java.util.Arrays;
import java.util.Objects;

import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.FileId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;

// Assuming Year class/enum exists in scope (e.g., in this package or imported)
public record User(
        UserId id,
        String username,
        String name,
        String email,
        String password,
        Year year,
        ReviewId[] reviewIds,
        CommentId[] commentIds,
        UserId[] contactIds,
        FileId profileImageId,
        String bio
        ) {

    // Compact constructor for validation and defensive copying of arrays on input
    public User           {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
        // id, name, and year can be null, matching original behavior where they might not be set initially.

        // Ensure array fields are non-null and defensively copied
        reviewIds = (reviewIds != null) ? Arrays.copyOf(reviewIds, reviewIds.length) : new ReviewId[0];
        commentIds = (commentIds != null) ? Arrays.copyOf(commentIds, commentIds.length) : new CommentId[0];
        contactIds = (contactIds != null) ? Arrays.copyOf(contactIds, contactIds.length) : new UserId[0];
    }

    // Convenience constructor to match the original 3-argument one
    public User(String username, String email, String password) {
        this(null, // id
                username,
                null, // name
                email,
                password,
                null, // year
                new ReviewId[0], // reviewIds
                new CommentId[0], // commentIds
                new UserId[0], // contactIds
                null, // profileImageId
                null // bio
        );
        // The call to this(...) will invoke the compact constructor for validations and array initializations.
    }

    // Override accessors for array fields to return defensive copies on output,
    // maintaining encapsulation and immutability.
    // The compact constructor ensures these arrays are never null.
    @Override
    public ReviewId[] reviewIds() {
        return Arrays.copyOf(this.reviewIds, this.reviewIds.length);
    }

    @Override
    public CommentId[] commentIds() {
        return Arrays.copyOf(this.commentIds, this.commentIds.length);
    }

    @Override
    public UserId[] contactIds() {
        return Arrays.copyOf(this.contactIds, this.contactIds.length);
    }
}
