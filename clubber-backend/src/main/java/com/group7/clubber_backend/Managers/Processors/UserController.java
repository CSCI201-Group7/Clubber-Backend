package com.group7.clubber_backend;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.clubber_backend.Managers.UserManager;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Schemas.Users.DeleteResponse;
import com.group7.lib.types.Schemas.Users.GetResponse;
import com.group7.lib.types.Schemas.Users.PostRequest;
import com.group7.lib.types.Schemas.Users.PostResponse;
import com.group7.lib.types.Schemas.Users.PutRequest;
import com.group7.lib.types.User.User;
import com.group7.lib.types.User.Year;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserManager userManager;

    public UserController() {
        this.userManager = UserManager.getInstance();
    }

    @GetMapping("/{id}")
    public GetResponse getUser(@PathVariable String id) {
        User user = this.userManager.get(new UserId(id));
        return new GetResponse(user);
    }

    @PostMapping("/")
    public PostResponse createUser(@RequestBody PostRequest request) {
        User user = new User(
            new UserId(java.util.UUID.randomUUID().toString()),
            request.username(),
            request.name(),
            request.email(),
            Year.valueOf(request.year()),
            request.major(),
            new Date(),
            new Date(),
            new com.group7.lib.types.Ids.ReviewId[0],
            new com.group7.lib.types.Ids.CommentId[0],
            new com.group7.lib.types.Ids.OrganizationId[0],
            new UserId[0],
            new com.group7.lib.types.User.Favorites(
                new com.group7.lib.types.Ids.ReviewId[0],
                new com.group7.lib.types.Ids.OrganizationId[0]
            )
        );
        this.userManager.create(user);
        return new PostResponse(user);
    }

    @PutMapping("/{id}")
    public GetResponse updateUser(@PathVariable String id, @RequestBody PutRequest request) {
        User user = this.userManager.get(new UserId(id));
        user.setUsername(request.username());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setYear(Year.valueOf(request.year()));
        user.setMajor(request.major());
        this.userManager.update(user);
        return new GetResponse(user);
    }

    @DeleteMapping("/{id}")
    public DeleteResponse deleteUser(@PathVariable String id) {
        try {
            this.userManager.delete(new UserId(id));
            return new DeleteResponse(true, "User deleted successfully");
        } 
        catch (Exception e) {
            return new DeleteResponse(false, "Failed to delete user: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/reviews")
    public List<com.group7.lib.types.Review.Review> getUserReviews(@PathVariable String id) {
        return this.userManager.getUserReviews(new UserId(id));
    }

    @GetMapping("/{id}/comments")
    public List<com.group7.lib.types.Comment.Comment> getUserComments(@PathVariable String id) {
        return this.userManager.getUserComments(new UserId(id));
    }

    @GetMapping("/{id}/favorites")
    public List<com.group7.lib.types.Organization.Organization> getUserFavorites(@PathVariable String id) {
        return this.userManager.getUserFavorites(new UserId(id));
    }

    @PostMapping("/{id}/interests")
    public void addUserInterests(@PathVariable String id, @RequestBody List<String> interests) {
        this.userManager.addUserInterests(new UserId(id), interests);
    }

    @GetMapping("/{id}/recommendations")
    public List<com.group7.lib.types.Organization.Organization> getUserRecommendations(@PathVariable String id) {
        return this.userManager.getUserRecommendations(new UserId(id));
    }
}
