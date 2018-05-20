package com.github.elementbound.nchess.event;

/**
 * Interface to represent callbacks. Event listeners called when an event source
 * emits an event
 * @see EventSource
 * @param <T> event data type
 */
@FunctionalInterface
public interface EventListener<T> {
    void on(T event);
}
