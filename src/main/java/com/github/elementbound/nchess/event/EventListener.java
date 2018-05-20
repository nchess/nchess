package com.github.elementbound.nchess.event;

@FunctionalInterface
public interface EventListener<T> {
    void on(T event);
}
