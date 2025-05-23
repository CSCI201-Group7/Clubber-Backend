package com.group7.clubber_backend;

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
import org.springframework.web.server.ResponseStatusException;

import com.group7.clubber_backend.Managers.CommentManager;
import com.group7.clubber_backend.Managers.ReviewManager;
import com.group7.clubber_backend.Managers.UserManager;
import com.group7.clubber_backend.Processors.CredentialProcessor;
import com.group7.lib.types.Comment.Comment;
import com.group7.lib.types.Ids.CommentId;
import com.group7.lib.types.Ids.ReviewId;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Review.Review;
import com.group7.lib.types.Schemas.Comments.GetResponse;
import com.group7.lib.types.Schemas.Comments.PostRequest;
import com.group7.lib.types.Schemas.Events.VoteRequest;
import com.group7.lib.types.Schemas.ListResponse;
import com.group7.lib.types.Schemas.PostResponse;
import com.group7.lib.types.User.User;

@RestController
@RequestMapping("/comments") // Nested under reviews
public class CommentController {

    private final CommentManager commentManager = CommentManager.getInstance();
    private final UserManager userManager = UserManager.getInstance();
    private final ReviewManager reviewManager = ReviewManager.getInstance(); // To check if review exists
    private final CredentialProcessor credentialProcessor = CredentialProcessor.getInstance();

    // Create a new comment on a review
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public PostResponse createComment(
            @RequestHeader("Authorization") String token,
            @RequestBody PostRequest request) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Token missing");
        }
        UserId authorUserId = credentialProcessor.verifyToken(token);
        if (authorUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or expired token");
        }
        User author = userManager.get(authorUserId);
        if (author == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: User not found");
        }

        Review review = null;
        if (request.reviewId() != null) {
            review = reviewManager.get(new ReviewId(request.reviewId()));
            if (review == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
            }
        }

        // Check if the parent comment exists
        Comment parentComment = null;
        if (request.parentCommentId() != null) {
            parentComment = commentManager.get(new CommentId(request.parentCommentId()));
            if (parentComment == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent comment not found");
            }
        }

        if (review == null && parentComment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review or parent comment is required");
        }

        Comment newComment = new Comment(
                null, // ID will be generated by the manager
                authorUserId,
                parentComment == null ? null : parentComment.id(),
                review == null ? null : review.id(),
                request.text(),
                LocalDateTime.now().toString(),
                new UserId[]{authorUserId},
                new UserId[0]
        );

        CommentId commentId = (CommentId) commentManager.create(newComment);
        if (commentId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create comment");
        }

        return new PostResponse(commentId);
    }

    // Get a specific comment by its ID
    @GetMapping("/{commentIdStr}")
    public GetResponse getCommentById(@PathVariable String commentIdStr) {
        CommentId commentId = new CommentId(commentIdStr);
        Comment comment = commentManager.get(commentId);

        if (comment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        return new GetResponse(comment);
    }

    // Get all comments for a specific review
    @GetMapping
    public ListResponse<GetResponse> queryComments(
            @RequestParam(value = "reviewId", required = false) String reviewIdStr,
            @RequestParam(value = "commentId", required = false) String commentIdStr,
            @RequestParam(value = "userId", required = false) String userIdStr
    ) {
        if (reviewIdStr == null && commentIdStr == null && userIdStr == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "reviewId or commentId or userId is required");
        }
        if (reviewIdStr != null && commentIdStr != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "reviewId and commentId cannot both be provided");
        }
        if (reviewIdStr != null) {
            ReviewId reviewId = new ReviewId(reviewIdStr);
            if (reviewManager.get(reviewId) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
            }
            List<Comment> comments = commentManager.search("reviewId:" + reviewId);
            return ListResponse.fromList(comments.stream().map(GetResponse::new).collect(Collectors.toList()));
        }
        if (commentIdStr != null) {
            CommentId commentId = new CommentId(commentIdStr);
            if (commentManager.get(commentId) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
            }
            List<Comment> comments = commentManager.search("parentCommentId:" + commentId);
            return ListResponse.fromList(comments.stream().map(GetResponse::new).collect(Collectors.toList()));
        }
        if (userIdStr != null) {
            UserId userId = new UserId(userIdStr);
            List<Comment> comments = commentManager.search("userId:" + userId);
            return ListResponse.fromList(comments.stream().map(GetResponse::new).collect(Collectors.toList()));
        }

        return new ListResponse<>(Collections.emptyList());
    }

    // Update an existing comment
    @PutMapping("/{commentIdStr}")
    public void updateComment(@RequestHeader("Authorization") String token,
            @PathVariable String commentIdStr,
            @RequestParam("text") String text) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Token missing");
        }
        UserId currentUserId = credentialProcessor.verifyToken(token);
        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or expired token");
        }

        CommentId commentId = new CommentId(commentIdStr);
        Comment existingComment = commentManager.get(commentId);

        if (existingComment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        // Check if the current user is the author of the comment
        if (!existingComment.userId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden: You are not the author of this comment");
        }

        Comment updatedComment = new Comment(
                existingComment.id(),
                existingComment.userId(),
                existingComment.parentCommentId(),
                existingComment.reviewId(),
                text,
                existingComment.createdAt(),
                existingComment.upvotes(),
                existingComment.downvotes()
        );

        commentManager.update(updatedComment);
    }

    // Delete a comment
    @DeleteMapping("/{commentIdStr}")
    public void deleteComment(@RequestHeader("Authorization") String token,
            @PathVariable String commentIdStr) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Token missing");
        }
        UserId currentUserId = credentialProcessor.verifyToken(token);
        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid or expired token");
        }

        CommentId commentId = new CommentId(commentIdStr);
        Comment commentToDelete = commentManager.get(commentId);

        if (commentToDelete == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        // Authorization: Check if current user is the author. 
        // Add admin/review author deletion rights if necessary.
        if (!commentToDelete.userId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden: You are not authorized to delete this comment");
        }

        commentManager.delete(commentId);
    }

    // Upvote a comment
    @PutMapping(value = "/{commentIdStr}/upvote", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void upvoteComment(
            @RequestHeader("Authorization") String token,
            @PathVariable String commentIdStr,
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

        CommentId commentObjectId = new CommentId(commentIdStr);
        Comment existingComment = commentManager.get(commentObjectId);

        if (existingComment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        List<UserId> newUpvotes = new ArrayList<>(Arrays.asList(existingComment.upvotes()));
        List<UserId> newDownvotes = new ArrayList<>(Arrays.asList(existingComment.downvotes()));

        if (request.revoke()) {
            newUpvotes.remove(currentUserId);
        } else {
            newUpvotes.add(currentUserId);
            if (newDownvotes.contains(currentUserId)) {
                newDownvotes.remove(currentUserId);
            }
        }

        Comment updatedComment = new Comment(
                existingComment.id(),
                existingComment.userId(),
                existingComment.parentCommentId(),
                existingComment.reviewId(),
                existingComment.text(),
                existingComment.createdAt(),
                newUpvotes.toArray(UserId[]::new),
                newDownvotes.toArray(UserId[]::new)
        );

        commentManager.update(updatedComment);
    }

    // Downvote a comment
    @PutMapping(value = "/{commentIdStr}/downvote", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void downvoteComment(
            @RequestHeader("Authorization") String token,
            @PathVariable String commentIdStr,
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

        CommentId commentObjectId = new CommentId(commentIdStr);
        Comment existingComment = commentManager.get(commentObjectId);

        if (existingComment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        List<UserId> newUpvotes = new ArrayList<>(Arrays.asList(existingComment.upvotes()));
        List<UserId> newDownvotes = new ArrayList<>(Arrays.asList(existingComment.downvotes()));

        if (request.revoke()) {
            newDownvotes.remove(currentUserId);
        } else {
            newDownvotes.add(currentUserId);
            if (newUpvotes.contains(currentUserId)) {
                newUpvotes.remove(currentUserId);
            }
        }

        Comment updatedComment = new Comment(
                existingComment.id(),
                existingComment.userId(),
                existingComment.parentCommentId(),
                existingComment.reviewId(),
                existingComment.text(),
                existingComment.createdAt(),
                newUpvotes.toArray(UserId[]::new),
                newDownvotes.toArray(UserId[]::new)
        );

        commentManager.update(updatedComment);
    }
}
