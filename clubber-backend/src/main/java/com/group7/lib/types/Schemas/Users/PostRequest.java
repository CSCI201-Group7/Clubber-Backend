package com.group7.lib.types.Schemas.Users;

public record PostRequest(
    String username,
    String name,
    String email,
    String year,
    String major
) {}
