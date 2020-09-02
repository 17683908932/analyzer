package com.test.utils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.stream.Stream;

public class CollectionUtil {
	public static<T> boolean arrayContainAny(T[] arrays, T... contains) {
		if (arrays == null || arrays.length == 0) { return false; }
		if (contains == null || contains.length == 0) { return true; }
		return Arrays.stream(arrays).anyMatch(one -> {
			return Arrays.stream(contains).anyMatch(two -> one.equals(two));
		});
	}

}
