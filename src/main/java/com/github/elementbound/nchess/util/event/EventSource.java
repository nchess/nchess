package com.github.elementbound.nchess.util.event;

import java.util.LinkedHashSet;
import java.util.Set;

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
