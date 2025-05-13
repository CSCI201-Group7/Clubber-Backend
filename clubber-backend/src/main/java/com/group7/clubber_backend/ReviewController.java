package com.group7.clubber_backend;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.group7.clubber_backend.Managers.FileManager;
import com.group7.clubber_backend.Managers.ReviewManager;
import com.group7.clubber_backend.Managers.UserManager;
import com.group7.clubber_backend.Processors.CredentialProcessor;
import com.group7.lib.types.Ids.FileId;
import com.group7.lib.types.Ids.OrganizationId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Review.Review;
import com.group7.lib.types.Schemas.ListResponse;
import com.group7.lib.types.Schemas.PostResponse;
import com.group7.lib.types.Schemas.Reviews.GetResponse;
import com.group7.lib.types.Schemas.Reviews.VoteRequest;
import com.group7.lib.types.User.User;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewManager reviewManager = ReviewManager.getInstance();
    private final UserManager userManager = UserManager.getInstance();
    private final CredentialProcessor credentialProcessor = CredentialProcessor.getInstance();
    private final FileManager fileManager = FileManager.getInstance();

    @PostMapping
    public PostResponse createReview(
            @RequestHeader("Authorization") String token,
            @RequestParam("organizationId") String organizationIdStr,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("communityRating") int communityRating,
            @RequestParam("activitiesRating") int activitiesRating,
            @RequestParam("leadershipRating") int leadershipRating,
            @RequestParam("inclusivityRating") int inclusivityRating,
            @RequestParam("overallRating") int overallRating,
            @RequestParam("attachments") List<MultipartFile> attachments) {
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
            organizationId = new OrganizationId(organizationIdStr);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid organization ID format");
        }

        if (overallRating < 1 || overallRating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Overall rating must be between 1 and 5");
        }

        List<FileId> fileIds = new ArrayList<>();
        for (MultipartFile attachment : attachments) {
            try {
                FileId fileId = (FileId) fileManager.upload(
                        attachment.getOriginalFilename(),
                        attachment.getInputStream(),
                        attachment.getContentType());
                fileIds.add(fileId);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload attachment");
            }
        }

        Review.Rating rating = new Review.Rating(
                overallRating,
                communityRating,
                activitiesRating,
                leadershipRating,
                inclusivityRating
        );

        Review newReview = new Review(
                null,
                authorUserId,
                organizationId,
                title,
                content,
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

    @GetMapping("/all")
    public ListResponse<GetResponse> getAllReviews() {
        List<Review> reviews = reviewManager.getAll();
        return ListResponse.fromList(reviews.stream().map(GetResponse::new).collect(Collectors.toList()));
    }

    @GetMapping
    public ListResponse<GetResponse> getReviewsByUser(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "organizationId", required = false) String organizationId
    ) {
        if (userId == null && organizationId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId or organizationId is required");
        }
        if (userId != null && organizationId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId and organizationId cannot both be provided");
        }
        List<Review> reviews;
        if (userId != null) {
            reviews = reviewManager.search("authorId:" + userId);
        } else {
            reviews = reviewManager.search("organizationId:" + organizationId);
        }
        return ListResponse.fromList(reviews.stream().map(GetResponse::new).collect(Collectors.toList()));
    }

    @PutMapping(value = "/{reviewIdStr}/upvote", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void upvoteReview(
            @RequestHeader("Authorization") String token,
            @PathVariable String reviewIdStr,
            @RequestBody VoteRequest request) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Token missing");
        }
        UserId currentUserId = credentialProcessor.verifyToken(token);
        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or expired token");
        }

        User currentUser = userManager.get(currentUserId);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        ReviewId reviewObjectId = new ReviewId(reviewIdStr);
        Review existingReview = reviewManager.get(reviewObjectId);

        if (existingReview == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }

        if (!existingReview.authorId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden: You are not the author of this review");
        }

        ArrayList<UserId> newUpvotes = new ArrayList<>(existingReview.upvotes());
        ArrayList<UserId> newDownvotes = new ArrayList<>(existingReview.downvotes());

        if (request.revoke()) {
            newUpvotes.remove(currentUserId);
        } else {
            newUpvotes.add(currentUserId);
        }

        Review updatedReview = new Review(
                existingReview.id(),
                existingReview.authorId(),
                existingReview.organizationId(),
                existingReview.title(),
                existingReview.content(),
                existingReview.rating(),
                existingReview.fileIds(),
                existingReview.createdAt(),
                LocalDateTime.now(),
                newUpvotes,
                newDownvotes,
                existingReview.commentIds()
        );

        reviewManager.update(updatedReview);
    }

    @PutMapping(value = "/{reviewIdStr}/downvote", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void downvoteReview(
            @RequestHeader("Authorization") String token,
            @PathVariable String reviewIdStr,
            @RequestBody VoteRequest request) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Token missing");
        }
        UserId currentUserId = credentialProcessor.verifyToken(token);
        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or expired token");
        }

        User currentUser = userManager.get(currentUserId);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        ReviewId reviewObjectId = new ReviewId(reviewIdStr);
        Review existingReview = reviewManager.get(reviewObjectId);

        if (existingReview == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }

        if (!existingReview.authorId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden: You are not the author of this review");
        }

        ArrayList<UserId> newUpvotes = new ArrayList<>(existingReview.upvotes());
        ArrayList<UserId> newDownvotes = new ArrayList<>(existingReview.downvotes());

        if (request.revoke()) {
            newDownvotes.remove(currentUserId);
        } else {
            newDownvotes.add(currentUserId);
        }

        Review updatedReview = new Review(
                existingReview.id(),
                existingReview.authorId(),
                existingReview.organizationId(),
                existingReview.title(),
                existingReview.content(),
                existingReview.rating(),
                existingReview.fileIds(),
                existingReview.createdAt(),
                LocalDateTime.now(),
                newUpvotes,
                newDownvotes,
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
