package com.group7.clubber_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.clubber_backend.Managers.UserManager;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Schemas.Users.GetResponse;
import com.group7.lib.types.Schemas.Users.PostRequest;
import com.group7.lib.types.Schemas.Users.PostResponse;
import com.group7.lib.types.User.User;

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
        // User user = this.userManager.create(request);
        // return new PostResponse(user);
    }
}
