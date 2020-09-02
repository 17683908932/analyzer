package com.test.define;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class DefinedRandom {

	public static Integer randomInteger(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	public static Double randomDouble(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	public static Long randomLong(long min, long max) {
		return ThreadLocalRandom.current().nextLong(min, max);
	}

	public static Boolean randomBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}

	public static String randomString(int minLength, int maxLength) {
		StringBuilder builder = new StringBuilder();
		int length = ThreadLocalRandom.current().nextInt(minLength, maxLength);
		for (int i = 0; i < length; i++) {
			builder.append((char) ThreadLocalRandom.current().nextInt('a', 'Z'+1));
		}
		return builder.toString();
	}

	public static BigDecimal randomBigDecimal(BigDecimal min, BigDecimal max, int dotNum) {
		double minDouble = min.doubleValue();
		double maxDouble = max.doubleValue();
		Double randomDouble = randomDouble(minDouble, maxDouble);
		BigDecimal randomDecimal = new BigDecimal(randomDouble.toString());
		randomDecimal.setScale(dotNum, RoundingMode.DOWN);
		return randomDecimal;
	}

	public static Date randomDate(Date min, Date max) {
		long minTime = min.getTime();
		long maxTime = max.getTime();
		Long randomTime = randomLong(minTime, maxTime);
		return new Date(randomTime);
	}

	public static Enum randomEnum(Class<Enum> enumClass) {
		Object[] enums = enumClass.getEnumConstants();
		if (enums.length == 1) { return (Enum) enums[0]; }
		int index = ThreadLocalRandom.current().nextInt(0, enums.length);
		Object obj = null;
		while (index > 0 && (obj = enums[index]) == null) {
			index--;
		}
		return (Enum) obj;
	}
}
