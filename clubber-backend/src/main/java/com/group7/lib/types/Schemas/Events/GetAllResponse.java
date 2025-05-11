package com.group7.lib.types.Schemas.Events;

import java.util.List;

import com.group7.lib.types.Event.Event;

public class GetAllResponse {
    private List<Event> events;

    public GetAllResponse(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
} 