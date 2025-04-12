package com.group7.lib.types.Schemas.Users;
import com.group7.lib.types.User.User;
import java.util.Date;

public record PostResponse(
    String id,
    String username,
    String name,
    String email,
    String year,
    String major,
    Date registrationDate,
    Date lastLogin
) {
    public PostResponse(User user) {
        this(
            user.getId().toString(),
            user.getUsername(),
            user.getName(),
            user.getEmail(),
            user.getYear().toString(),
            user.getMajor(),
            user.getRegistrationDate(),
            user.getLastLogin()
        );
    }
}
