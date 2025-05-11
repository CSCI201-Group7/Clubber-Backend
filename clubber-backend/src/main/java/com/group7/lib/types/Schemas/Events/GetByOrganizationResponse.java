package com.group7.lib.types.Schemas.Events;

import java.util.List;

import com.group7.lib.types.Event.Event;

public class GetByOrganizationResponse {
    private List<Event> events;

    public GetByOrganizationResponse(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
} 