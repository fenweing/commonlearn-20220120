package com.tuanbaol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @description: CollectionUtil
 * @version: 1.0.0
 */
public final class CollectionUtil  {
    private CollectionUtil() {
    }

    public static <T> ArrayList<T> ofList(T... ts) {
        ArrayList<T> arrayList = new ArrayList<>();
        if (ts == null || ts.length <= 0) {
            return arrayList;
        }
        for (T t : ts) {
            arrayList.add(t);
        }
        return arrayList;
    }

    public static <T> LinkedList<T> ofLinkedList(T... ts) {
        return new LinkedList<>(ofList(ts));
    }

    public static <T> HashSet<T> ofHashSet(T... ts) {
        return new HashSet<>(ofList(ts));
    }

}
