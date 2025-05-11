package com.group7.lib.types.Schemas.Events;

import com.group7.lib.types.Event.Event;

public class PutResponse {
    private Event event;
    private String message;

    public PutResponse(Event event, String message) {
        this.event = event;
        this.message = message;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 