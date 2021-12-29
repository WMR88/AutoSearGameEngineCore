package Observers.Events;

import ASGE.GameObject;

public class Event {
    public EventType type;

    public Event(EventType type) {
        this.type = type;
    }

    public Event() {
        this.type = EventType.UserEvent;
    }
}
