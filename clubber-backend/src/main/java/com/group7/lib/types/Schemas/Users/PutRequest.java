package com.group7.lib.types.Schemas.Users;

public record PutRequest(
    String username,
    String name,
    String email,
    String year
) {} 