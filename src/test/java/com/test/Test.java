package com.test;

import com.test.analyzer.SpringUrlAnalyzer;

public class Test {
	public static void main(String[] args) throws Exception {
		SpringUrlAnalyzer urlAnalyzer = new SpringUrlAnalyzer("www.baidu.com");
		urlAnalyzer.analyzePackage("com.test.controller");
	}
}
