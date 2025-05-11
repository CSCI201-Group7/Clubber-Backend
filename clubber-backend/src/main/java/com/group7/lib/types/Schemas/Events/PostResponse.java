package com.group7.lib.types.Schemas.Events;

import com.group7.lib.types.Ids.EventId;

public class PostResponse {
    private EventId eventId;
    private String message;

    public PostResponse(EventId eventId, String message) {
        this.eventId = eventId;
        this.message = message;
    }

    public EventId getEventId() {
        return eventId;
    }

    public void setEventId(EventId eventId) {
        this.eventId = eventId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 