package com.github.elementbound.nchess.util.event;

@FunctionalInterface
public interface EventListener<T> {
    void on(T event);
}
