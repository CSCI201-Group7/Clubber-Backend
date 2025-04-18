package com.group7.lib.types.Schemas.Users;

import com.group7.lib.types.User.User;

public record PostResponse(
		String id,
		String username,
        String name,
        String email,
        String year,
        String[] reviewIds,
        String[] commentIds,
        String[] organizationIds,
        String[] contactIds,
        String[] favoriteReviewIds,
        String[] favoriteOrganizationIds) {
	
	public PostResponse(User user) {
		this(
				user.getId().toString(),
				user.getUsername(),
				user.getName(),
				user.getEmail(),
				user.getYear().toString(),
				convertToStringArray(user.getReviewIds()),
                convertToStringArray(user.getCommentIds()),
                convertToStringArray(user.getOrganizationIds()),
                convertToStringArray(user.getContactIds()),
                convertToStringArray(user.getFavorites().getReviewIds()),
                convertToStringArray(user.getFavorites().getOrganizationIds())
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
