package com.test.utils;

import java.io.File;
import java.net.URL;

public class FileUtil {
	public static File packageToFile(String packageName) {
		URL url = ClassLoader.getSystemResource(packageName.replace('.', '/'));
		File file = new File(url.getPath());
		return file;
	}

	public static String fileNameRemoveSuffix(String fileName) {
		int index = fileName.lastIndexOf('.');
		return fileName.substring(0, index);
	}
}
