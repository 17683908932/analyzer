package com.test.analyzer;

import com.test.define.DefinedClass;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.test.utils.FileUtil.*;

public abstract class ClassAnalyzer implements Analyzer {

	protected final Map<String, List<DefinedClass>> packageMapClasses = new HashMap<>();

	@Override
	public abstract Object analyzePackage(String packageName) throws Exception;

	protected final List<DefinedClass> analysisFiles(File pathFile, String packageName) {
		List<DefinedClass> classes = new ArrayList<>();
		File[] files = pathFile.listFiles();
		if (files == null || files.length == 0) { return classes; }
		for (File file : files) {
			if (file.isFile()) {
				if (file.getName().endsWith(".class")) {
					try {
						Class clazz = Class.forName(packageName+"."+fileNameRemoveSuffix(file.getName()));
						classes.add(DefinedClass.getDefinedClazz(clazz));
					} catch (ClassNotFoundException e) {
						continue;
					}
				}
				continue;
			}
			if (file.isDirectory()) {
				analysisFiles(file, packageName+"."+file.getName());
			}
		}
		if (classes.size() == 0) { return classes; }
		packageMapClasses.put(packageName, classes);
		return classes;
	}

}
