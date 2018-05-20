package com.github.elementbound.nchess.util;

import com.github.elementbound.nchess.game.Node;

/**
 * Helper functions to deal with {@link com.github.elementbound.nchess.game.Table} instances.
 */
public final class TableUtils {
    public static double linkDirection(Node a, Node b) {
        return MathUtils.vectorDirection(a.getX(), a.getY(), b.getX(), b.getY());
    }

    private TableUtils() {
        // Hide constructor
    }
}
