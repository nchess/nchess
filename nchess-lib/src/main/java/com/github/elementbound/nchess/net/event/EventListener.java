package com.github.elementbound.nchess.net.event;

@FunctionalInterface
public interface EventListener<T> {
    void on(T event);
}
