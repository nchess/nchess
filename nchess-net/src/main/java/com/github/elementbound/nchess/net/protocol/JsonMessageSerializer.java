package com.github.elementbound.nchess.net.protocol;

/**
 * Interface to convert a message to JSON string.
 */
public interface JsonMessageSerializer {
    String toJSON();
}
