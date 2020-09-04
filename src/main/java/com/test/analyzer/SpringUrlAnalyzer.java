package com.test.analyzer;

import com.alibaba.fastjson.JSONObject;
import com.test.define.*;
import com.test.params.RequestParamsParser;
import com.test.utils.CollectionUtil;
import com.test.utils.FileUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class SpringUrlAnalyzer extends ClassAnalyzer {

	private String url;
	private RequestParamsParser paramsParser;
	private List<DefinedRequestUrl> requestUrls = new ArrayList<>();

	@Override
	public File analyzePackage(String packageName) throws Exception {
		File baseFile = FileUtil.packageToFile(packageName);
		analysisFiles(baseFile, packageName);
		analyzeClasses();
		generaterFile(baseFile);
		return null;
	}

	protected File generaterFile(File baseFile) throws Exception {
		File urlFile = new File(baseFile, "url.txt");
		if (requestUrls.size() > 0) {
			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(urlFile), "UTF-8"));) {
				for (DefinedRequestUrl requestUrl : requestUrls) {
					String[] urls = requestUrl.toUrl();
					for (int i = 0; i < urls.length; i++) {
						writer.write(urls[i]);
						writer.newLine();
					}
				}
				writer.flush();
			}
			return urlFile;
		}
		return urlFile;
	}

	protected void analyzeClasses() {
		Map<String, List<DefinedClass>> packageMapClass = packageMapClasses;
		for (Map.Entry<String, List<DefinedClass>> entry : packageMapClass.entrySet()) {
			List<DefinedClass> definedClasses = entry.getValue();
			if (definedClasses != null && definedClasses.size() > 0) {
				for (DefinedClass definedClass : definedClasses) {
					classToRequestUrls(definedClass);
				}
			}
		}
	}

	protected void classToRequestUrls(DefinedClass definedClass) {
		DefinedRequestUrl definedRequestUrl = classDefinedUrl(definedClass);
		if (definedRequestUrl != null) {
			Map<Method, DefinedMethod> methodMap = definedClass.getMethods();
			if (!CollectionUtil.isEmpty(methodMap)) {
				for (Map.Entry<Method, DefinedMethod> methodEntry : methodMap.entrySet()) {
					DefinedMethod definedMethod = methodEntry.getValue();
					methodDefinedUrl(definedRequestUrl, definedMethod);
				}
			}
		}
	}

	protected DefinedRequestUrl methodDefinedUrl(DefinedRequestUrl definedUrl, DefinedMethod method) {
		DefinedRequestUrl methodDefinedUrl = null;
		RequestMapping methodMapping;
		GetMapping methodGetMapping;
		PostMapping methodPostMapping;
		if ((methodMapping = method.getAnnotations(RequestMapping.class)) != null) {
			methodDefinedUrl = definedUrl.clone();
			RequestMethod[] requestMethods = methodMapping.method();
			if (!CollectionUtil.isEmpty(requestMethods)) {
				String[] methods = new String[requestMethods.length];
				for (int i = 0; i < requestMethods.length; i++) {
					methods[i] = requestMethods[i].name();
				}
				methodDefinedUrl.setMethod(methods);
			} else {
				methodDefinedUrl.setMethod(new String[]{"POST"});
			}
			methodDefinedUrl.setSecondDomain(methodMapping.value());
		} else if ((methodGetMapping = method.getAnnotations(GetMapping.class)) != null) {
			methodDefinedUrl = definedUrl.clone();
			methodDefinedUrl.setMethod(new String[]{"GET"});
			methodDefinedUrl.setSecondDomain(methodGetMapping.value());
		} else if ((methodPostMapping = method.getAnnotations(PostMapping.class)) != null) {
			methodDefinedUrl = definedUrl.clone();
			methodDefinedUrl.setMethod(new String[]{"POST"});
			methodDefinedUrl.setSecondDomain(methodPostMapping.value());
		}

		if (methodDefinedUrl != null) {
			Parameter[] parameters = method.getParameters();
			if (parameters != null && parameters.length > 0) {
				List<DefinedRequestParams> definedRequestParams = paramsParser.methodParseParams(method);

				if (parameters.length == 1 && parameters[0].getDeclaredAnnotation(RequestBody.class) != null) {
					methodDefinedUrl.setParams(DefinedRequestParams.parseJsonString(definedRequestParams.get(0)));
				} else {
					if (definedRequestParams.size() == 1) {
						methodDefinedUrl.setParams(DefinedRequestParams.parseFormString(definedRequestParams.get(0)));
					} else {
						methodDefinedUrl.setParams(DefinedRequestParams.parseFormString(definedRequestParams));
					}
				}
			}
			requestUrls.add(methodDefinedUrl);
			return methodDefinedUrl;
		}
		return null;
	}

	protected DefinedRequestUrl classDefinedUrl(DefinedClass definedClass) {
		if (definedClass.hasAnnotations(RestController.class)) {
			DefinedRequestUrl definedUrl = new DefinedRequestUrl();
			definedUrl.setUrl(url);
			RequestMapping classMapping = (RequestMapping) definedClass.getAnnotations(RequestMapping.class);
			if (classMapping != null) {
				definedUrl.setFirstDomain(classMapping.value());
			}
			return definedUrl;
		}
		return null;
	}

	protected String[] parameterName(Parameter parameter) {
		String name = parameter.getName();
		RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
		if (requestParam != null) {
			name = requestParam.value().length() == 0?requestParam.name():requestParam.value();
		}
		Class clazz = parameter.getType();
		if (clazz.getName().startsWith("java") || clazz.equals(MultipartFile.class)) {
			return new String[]{name};
		}

		Method[] mds = clazz.getDeclaredMethods();
		String[] allNames = new String[mds.length];
		int index = 0;
		for (int i = 0; i < mds.length; i++) {
			Method md = mds[i];
			if (Modifier.isPublic(md.getModifiers())) {
				String methodName = md.getName();
				char[] nameUpper = null;
				if (methodName.startsWith("set") && methodName.length() > 3) {
					nameUpper = methodName.substring(3).toCharArray();
				} else if (methodName.startsWith("is") && methodName.length() > 2) {
					nameUpper = methodName.substring(2).toCharArray();
				}
				if (nameUpper != null) {
					nameUpper[0] = Character.toLowerCase(nameUpper[0]);
					allNames[index++] = new String(nameUpper);
				}
			}
		}

		String[] names = new String[index];
		if (index == 0) { return names; }
		System.arraycopy(allNames, 0, names, 0, index);
		return names;
	}

	protected String[] paramsAppend(DefinedRequestUrl definedRequestUrl, Parameter parameter, boolean isJson) {
		String[] names = parameterName(parameter);
		if (names.length == 0) { return names; }

		if (isJson) {
			JSONObject paramsJson = new JSONObject();
			for (String name : names) {
				paramsJson.put(name, name);
			}
			definedRequestUrl.setParams(paramsJson.toJSONString());
		} else {
			for (String name : names) {
				definedRequestUrl.paramsAppend(name);
			}
		}
		return names;
	}

	public SpringUrlAnalyzer(String url) {
		this.url = url;
		paramsParser = new RequestParamsParser();
	}
}
