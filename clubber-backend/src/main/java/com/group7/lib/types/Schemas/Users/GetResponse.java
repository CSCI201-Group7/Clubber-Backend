package com.group7.lib.types.Schemas.Users;

import org.springframework.web.bind.annotation.ResponseBody;

import com.group7.lib.types.User.User;

@ResponseBody
public record GetResponse(
        String id,
        String username,
        String name,
        String email,
        String year,
        String[] reviewIds,
        String[] commentIds,
        String[] contactIds,
        String profileImageId,
        String bio) {

    public GetResponse(User user) {
        this(
                user.id() != null ? user.id().toString() : null,
                user.username(),
                user.name(),
                user.email(),
                user.year() != null ? user.year().toString() : null,
                convertToStringArray(user.reviewIds()),
                convertToStringArray(user.commentIds()),
                convertToStringArray(user.contactIds()),
                user.profileImageId() != null ? user.profileImageId().toString() : null,
                user.bio()
        );
    }

    private static String[] convertToStringArray(Object[] ids) {
        if (ids == null) {
            return new String[0];
        }
        String[] result = new String[ids.length];

        for (int i = 0; i < ids.length; i++) {
            result[i] = ids[i].toString();
        }
        return result;
    }
}
