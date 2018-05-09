package com.github.elementbound.nchess.util;

import com.github.elementbound.nchess.game.Node;

public class TableUtils {
    public static double linkDirection(Node a, Node b) {
        return MathUtils.vectorDirection(a.getX(), a.getY(), b.getX(), b.getY());
    }
}
