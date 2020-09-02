package com.test.define;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class DefinedMethod {
	private DefinedClass definedClass;
	private Method method;
	private String methodName;
	private Class returnType;
	private Parameter[] parameters;
	private Annotation[] annotations;

	public DefinedMethod(DefinedClass definedClass, Method method) {
		this.definedClass = definedClass;
		this.method = method;
		methodName = method.getName();
		returnType = method.getReturnType();
		parameters = method.getParameters();
		annotations = method.getAnnotations();
	}

	public String getMethodName() {
		return methodName;
	}

	public DefinedClass getDefinedClass() {
		return definedClass;
	}

	public void setDefinedClass(DefinedClass definedClass) {
		this.definedClass = definedClass;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Class getReturnType() {
		return returnType;
	}

	public void setReturnType(Class returnType) {
		this.returnType = returnType;
	}

	public Parameter[] getParameters() {
		return parameters;
	}

	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}
}
