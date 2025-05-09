package com.group7.clubber_backend;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.group7.clubber_backend.Managers.UserManager;
import com.group7.clubber_backend.Processors.CredentialProcessor;
import com.group7.lib.types.Ids.UserId;
import com.group7.lib.types.Schemas.Users.DeleteRequest;
import com.group7.lib.types.Schemas.Users.GetResponse;
import com.group7.lib.types.Schemas.Users.LoginRequest;
import com.group7.lib.types.Schemas.Users.PostRequest;
import com.group7.lib.types.Schemas.Users.PostResponse;
import com.group7.lib.types.User.User;
import com.group7.lib.utilities.Crypto.Crypto;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping
    public PostResponse register(@RequestBody PostRequest request) {
        if (request.username() == null || request.email() == null || request.password() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cannot be null");
        }
        String usernameEncrypted = request.username();
        String emailEncrypted = request.email();
        String passwordEncrypted = request.password();

        String username = CredentialProcessor.getInstance().decrypt(usernameEncrypted);
        String email = CredentialProcessor.getInstance().decrypt(emailEncrypted);
        String password = CredentialProcessor.getInstance().decrypt(passwordEncrypted);

        // Trim username and email to ensure consistency in checks and storage
        if (username != null) {
            username = username.trim();
        }
        if (email != null) {
            email = email.trim();
        }
        // It might also be a good idea to handle null or empty username/email explicitly here
        // depending on business rules, e.g., throw BAD_REQUEST if they are blank after trimming.

        if (username == null || username.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty");
        }
        if (email == null || email.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be empty");
        }

        if (!UserManager.getInstance().search("username:" + username).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (!UserManager.getInstance().search("email:" + email).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        String hashedPassword = Crypto.hash(password);
        User user = new User(username, email, hashedPassword);
        UserId userId = (UserId) UserManager.getInstance().create(user);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User registration failed");
        }
        String token = CredentialProcessor.getInstance().createToken(userId.toString());
        return new PostResponse(token);

    }

    @PostMapping("/login")
    public PostResponse login(@RequestBody LoginRequest request) {
        String emailEncrypted = request.email();
        String passwordEncrypted = request.password();

        String email = CredentialProcessor.getInstance().decrypt(emailEncrypted);
        String password = CredentialProcessor.getInstance().decrypt(passwordEncrypted);

        User user = UserManager.getInstance().search("email:" + email).get(0);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email");
        }
        System.out.println("User found: " + user.getId().toString());

        if (!Crypto.verify(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        String token = CredentialProcessor.getInstance().createToken(user.getId().toString());
        return new PostResponse(token);
    }

    @GetMapping("/{id}")
    public GetResponse getById(@PathVariable String id) {
        User user = UserManager.getInstance().get(new UserId(id));
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return new GetResponse(user);
    }

    @GetMapping
    public GetResponse get(@RequestHeader("Authorization") String token) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No token provided");
        }
        UserId userId = CredentialProcessor.getInstance().verifyToken(token);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        User user = UserManager.getInstance().get(userId);
        return new GetResponse(user);
    }

    @DeleteMapping
    public void delete(@RequestBody DeleteRequest request) {
        String token = request.token();
        UserId userId = CredentialProcessor.getInstance().verifyToken(token);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        try {
            UserManager.getInstance().delete(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User deletion failed");
        }
    }
}
