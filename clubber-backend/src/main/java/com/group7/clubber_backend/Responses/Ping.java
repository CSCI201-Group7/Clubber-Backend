package com.group7.clubber_backend.Responses;

public record Ping(String string) {
    public Ping(String string) {
        this.string = string;
    }
}
