package com.test.utils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Stream;

public class CollectionUtil {
	public static<T> boolean arrayContainAny(T[] arrays, T... contains) {
		if (arrays == null || arrays.length == 0) { return false; }
		if (contains == null || contains.length == 0) { return true; }
		return Arrays.stream(arrays).anyMatch(one -> {
			return Arrays.stream(contains).anyMatch(two -> one.equals(two));
		});
	}

	private static<T> List<T> collectFromArray(T[] arrays, Integer... indexes) {
		if (isEmpty(indexes)) { return null; }
		ArrayList<T> array = new ArrayList<>(indexes.length);
		for (int i = 0; i < indexes.length; i++) {
			if (indexes[i] < arrays.length) {
				array.add(arrays[indexes[i]]);
			}
		}
		return array;
	}

	public static boolean isEmpty(Collection collection) {
		return collection == null || collection.isEmpty();
	}

	public static<T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(Map map) {
		return map == null || map.isEmpty();
	}

}
