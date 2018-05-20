package com.github.elementbound.nchess.event;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>Class to represent an event source.
 * <p>Event sources emit events, which are then passed to listeners subscribed
 * to the event source, to react to said events.
 * @param <T> event data type
 */
public class EventSource<T> {
    private final Set<EventListener<T>> listeners = new LinkedHashSet<>();

    public EventSource<T> subscribe(EventListener<T> listener) {
        listeners.add(listener);
        return this;
    }

    public EventSource<T> unsubscribe(EventListener<T> listener) {
        listeners.remove(listener);
        return this;
    }

    public void emit(T event) {
        listeners.forEach(listener -> listener.on(event));
    }
}
