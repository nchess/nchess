package com.github.elementbound.nchess.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class CollectionUtils {
    private static final Random RANDOM = new Random();

    public static <T> T getRandomFrom(Collection<T> set) {
        int index = RANDOM.nextInt(set.size());
        Iterator<T> iterator = set.iterator();

        for(int i = 0; i < index; ++i) {
            iterator.next();
        }

        return iterator.next();
    }
}
