package com.group7.lib.types.Schemas;

public record Ping(String string) {
    public Ping(String string) {
        this.string = string;
    }
}
