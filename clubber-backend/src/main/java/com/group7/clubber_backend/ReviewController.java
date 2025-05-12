package com.group7.clubber_backend;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.group7.clubber_backend.Managers.ReviewManager;
import com.group7.clubber_backend.Managers.UserManager;
import com.group7.clubber_backend.Processors.CredentialProcessor;
import com.group7.lib.types.Ids.FileId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Review.Review;
import com.group7.lib.types.Schemas.Reviews.CreateReviewRequest;
import com.group7.lib.types.Schemas.Reviews.GetResponse;
import com.group7.lib.types.Schemas.Reviews.ListResponse;
import com.group7.lib.types.Schemas.Reviews.PostResponse;
import com.group7.lib.types.Schemas.Reviews.UpdateReviewRequest;
import com.group7.lib.types.User.User;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewManager reviewManager = ReviewManager.getInstance();
    private final UserManager userManager = UserManager.getInstance();
    private final CredentialProcessor credentialProcessor = CredentialProcessor.getInstance();

    @PostMapping
    public PostResponse createReview(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateReviewRequest request) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Token missing");
        }
        UserId authorUserId = credentialProcessor.verifyToken(token);
        if (authorUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or expired token");
        }

        User authorUser = userManager.get(authorUserId);
        if (authorUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        OrganizationId organizationId;
        try {
            organizationId = new OrganizationId(request.organizationId());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid organization ID format");
        }

        if (request.rating() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating details are required");
        }
        if (request.rating().overall() < 1 || request.rating().overall() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Overall rating must be between 1 and 5");
        }

        Review.Rating rating = new Review.Rating(
                request.rating().overall(),
                request.rating().community(),
                request.rating().activities(),
                request.rating().leadership(),
                request.rating().inclusivity()
        );

        List<FileId> fileIds = request.fileIds() != null
                ? request.fileIds().stream().map(FileId::new).collect(Collectors.toList())
                : Collections.emptyList();

        Review newReview = new Review(
                null,
                authorUserId,
                organizationId,
                request.title(),
                request.content(),
                rating,
                fileIds,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        ReviewId reviewId = (ReviewId) reviewManager.create(newReview);
        if (reviewId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create review");
        }

        // Update user's reviewIds
        ReviewId[] reviewIds = authorUser.reviewIds();
        List<ReviewId> reviewIdList = new ArrayList<>(Arrays.asList(reviewIds));
        reviewIdList.add(reviewId);
        User updatedUser = new User(
                authorUser.id(),
                authorUser.username(),
                authorUser.name(),
                authorUser.email(),
                authorUser.password(),
                authorUser.year(),
                reviewIdList.toArray(ReviewId[]::new),
                authorUser.commentIds(),
                authorUser.contactIds(),
                authorUser.profileImageId(),
                authorUser.bio()
        );
        userManager.update(updatedUser);
        return new PostResponse(reviewId);
    }

    @GetMapping("/{reviewId}")
    public GetResponse getReviewById(@PathVariable String reviewId) {
        Review review = reviewManager.get(new ReviewId(reviewId));
        if (review == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }
        return new GetResponse(review);
    }

    @GetMapping
    public ListResponse getAllReviews() {
        List<Review> reviews = reviewManager.getAll();
        return ListResponse.fromReviews(reviews);
    }

    @GetMapping("/users/{userId}")
    public ListResponse getReviewsByUser(@PathVariable String userId) {
        List<Review> reviews = reviewManager.search("authorId:" + userId);
        return ListResponse.fromReviews(reviews);
    }

    @GetMapping("/organizations/{organizationId}")
    public ListResponse getReviewsByOrganization(@PathVariable String organizationId) {
        List<Review> reviews = reviewManager.search("organizationId:" + organizationId);
        return ListResponse.fromReviews(reviews);
    }

    @PutMapping("/{reviewIdStr}")
    public void updateReview(
            @RequestHeader("Authorization") String token,
            @PathVariable String reviewIdStr,
            @RequestBody UpdateReviewRequest request) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Token missing");
        }
        UserId currentUserId = credentialProcessor.verifyToken(token);
        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or expired token");
        }

        ReviewId reviewObjectId = new ReviewId(reviewIdStr);
        Review existingReview = reviewManager.get(reviewObjectId);

        if (existingReview == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }

        if (!existingReview.authorId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden: You are not the author of this review");
        }

        String title = request.title() != null ? request.title() : existingReview.title();
        String content = request.content() != null ? request.content() : existingReview.content();

        Review.Rating newRating = existingReview.rating();
        if (request.rating() != null) {
            UpdateReviewRequest.RatingDto ratingDto = request.rating();
            if (ratingDto.overall() != null && (ratingDto.overall() < 1 || ratingDto.overall() > 5)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Overall rating must be between 1 and 5");
            }
            newRating = new Review.Rating(
                    ratingDto.overall() != null ? ratingDto.overall() : existingReview.rating().overall(),
                    ratingDto.community() != null ? ratingDto.community() : existingReview.rating().community(),
                    ratingDto.activities() != null ? ratingDto.activities() : existingReview.rating().activities(),
                    ratingDto.leadership() != null ? ratingDto.leadership() : existingReview.rating().leadership(),
                    ratingDto.inclusivity() != null ? ratingDto.inclusivity() : existingReview.rating().inclusivity()
            );
        }

        List<FileId> fileIds = request.fileIds() != null
                ? request.fileIds().stream().map(FileId::new).collect(Collectors.toList())
                : existingReview.fileIds();

        Review updatedReview = new Review(
                existingReview.id(),
                existingReview.authorId(),
                existingReview.organizationId(),
                title,
                content,
                newRating,
                fileIds,
                existingReview.createdAt(),
                LocalDateTime.now(),
                existingReview.upvotes(),
                existingReview.downvotes(),
                existingReview.commentIds()
        );

        reviewManager.update(updatedReview);
    }

    @DeleteMapping("/{reviewIdStr}")
    public void deleteReview(
            @RequestHeader("Authorization") String token,
            @PathVariable String reviewIdStr) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Token missing");
        }
        UserId currentUserId = credentialProcessor.verifyToken(token);
        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or expired token");
        }

        ReviewId reviewObjectId = new ReviewId(reviewIdStr);
        Review reviewToDelete = reviewManager.get(reviewObjectId);

        if (reviewToDelete == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }

        if (!reviewToDelete.authorId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden: You are not authorized to delete this review");
        }

        reviewManager.delete(reviewObjectId);
    }
}
