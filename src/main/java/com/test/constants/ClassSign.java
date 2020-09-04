package com.test.constants;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class ClassSign {
	public static final Set<Class> basicClasses = new HashSet<>();

	public static boolean isCollection(Class clazz) {
		return Collection.class.isAssignableFrom(clazz);
	}

	public static boolean needDepth(Class clazz) {
		if (isArray(clazz)) {
			Class arrayClass = clazz.getComponentType();
			return needDepth(arrayClass);
		} else {
			if (basicClasses.contains(clazz)) {
				return false;
			}
			if (MultipartFile.class.isAssignableFrom(clazz)) {
				return false;
			}
			if (clazz.getClassLoader() == null) {
				return false;
			}
			return true;
		}
	}

	public static boolean isArray(Class clazz) {
		return clazz.isArray();
	}

	public static boolean isMap(Class clazz) {
		return Map.class.isAssignableFrom(clazz);
	}

	static {
		basicClasses.add(boolean.class);
		basicClasses.add(Boolean.class);
		basicClasses.add(byte.class);
		basicClasses.add(Byte.class);
		basicClasses.add(short.class);
		basicClasses.add(Short.class);
		basicClasses.add(int.class);
		basicClasses.add(Integer.class);
		basicClasses.add(char.class);
		basicClasses.add(Character.class);
		basicClasses.add(long.class);
		basicClasses.add(Long.class);
		basicClasses.add(float.class);
		basicClasses.add(Float.class);
		basicClasses.add(double.class);
		basicClasses.add(Double.class);
		basicClasses.add(BigDecimal.class);
		basicClasses.add(BigInteger.class);
		basicClasses.add(String.class);
		basicClasses.add(Date.class);
	}
}
