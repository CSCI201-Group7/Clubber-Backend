package com.group7.lib.types.Schemas.Events;

import com.group7.lib.types.Event.Event;

public class GetResponse {
    private Event event;

    public GetResponse(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
} 