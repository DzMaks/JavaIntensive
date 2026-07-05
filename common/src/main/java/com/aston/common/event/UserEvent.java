package com.aston.common.event;

import java.io.Serializable;

public class UserEvent implements Serializable {

    private String email;
    private EventType event;

    public UserEvent() {
    }

    public UserEvent(String email, EventType event) {
        this.email = email;
        this.event = event;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EventType getEvent() {
        return event;
    }

    public void setEvent(EventType event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "email='" + email + '\'' +
                ", event=" + event +
                '}';
    }
}