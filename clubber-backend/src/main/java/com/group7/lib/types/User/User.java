package com.group7.lib.types.User;

import java.util.Arrays;
import java.util.Objects;

import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;

public class User {

    private final UserId id;
    private String username;
    private String name;
    private String email;
    private Year year;
    private ReviewId[] reviewIds;
    private CommentId[] commentIds;
    private OrganizationId[] organizationIds;
    private UserId[] contactIds;
    private Favorites favorites;

    public User(UserId id, String username, String name, String email, Year year, ReviewId[] reviewIds, CommentId[] commentIds, OrganizationId[] organizationIds, UserId[] contactIds, Favorites favorites) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.year = Objects.requireNonNull(year, "Year cannot be null");

        // Create defensive copies of arrays
        this.reviewIds = reviewIds != null ? Arrays.copyOf(reviewIds, reviewIds.length) : new ReviewId[0];
        this.commentIds = commentIds != null ? Arrays.copyOf(commentIds, commentIds.length) : new CommentId[0];
        this.organizationIds = organizationIds != null ? Arrays.copyOf(organizationIds, organizationIds.length) : new OrganizationId[0];
        this.contactIds = contactIds != null ? Arrays.copyOf(contactIds, contactIds.length) : new UserId[0];

        this.favorites = Objects.requireNonNull(favorites, "Favorites cannot be null");
    }

    public UserId getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username, "Username cannot be null");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email cannot be null");
    }

    public Year getYear() {
        return this.year;
    }

    public void setYear(Year year) {
        this.year = Objects.requireNonNull(year, "Year cannot be null");
    }

    public ReviewId[] getReviewIds() {
        return Arrays.copyOf(this.reviewIds, this.reviewIds.length);
    }

    public void setReviewIds(ReviewId[] reviewIds) {
        this.reviewIds = reviewIds != null
                ? Arrays.copyOf(reviewIds, reviewIds.length) : new ReviewId[0];
    }

    public CommentId[] getCommentIds() {
        return Arrays.copyOf(this.commentIds, this.commentIds.length);
    }

    public void setCommentIds(CommentId[] commentIds) {
        this.commentIds = commentIds != null
                ? Arrays.copyOf(commentIds, commentIds.length) : new CommentId[0];
    }

    public OrganizationId[] getOrganizationIds() {
        return Arrays.copyOf(this.organizationIds, this.organizationIds.length);
    }

    public void setOrganizationIds(OrganizationId[] organizationIds) {
        this.organizationIds = organizationIds != null
                ? Arrays.copyOf(organizationIds, organizationIds.length) : new OrganizationId[0];
    }

    public UserId[] getContactIds() {
        return Arrays.copyOf(this.contactIds, this.contactIds.length);
    }

    public void setContactIds(UserId[] contactIds) {
        this.contactIds = contactIds != null
                ? Arrays.copyOf(contactIds, contactIds.length) : new UserId[0];
    }

    public Favorites getFavorites() {
        return this.favorites;
    }

    public void setFavorites(Favorites favorites) {
        this.favorites = Objects.requireNonNull(favorites, "Favorites cannot be null");
    }
}
