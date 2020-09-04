package com.test.utils;

public class StringUtil {
	public static boolean isEmpty(CharSequence string) {
		if (string == null || string.length() == 0) {
			return true;
		}
		return false;
	}
}
