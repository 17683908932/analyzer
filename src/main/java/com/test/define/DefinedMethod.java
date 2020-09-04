package com.test.define;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class DefinedMethod {
	private DefinedClass definedClass;
	private Method method;
	private String methodName;
	private Class returnType;
	private ParameterizedType returnGenericType;
	private Parameter[] parameters;

	private boolean getter = false;
	private boolean setter = false;
	private DefinedField getterSetterField;
	private boolean returnVoid;

	public DefinedMethod(DefinedClass definedClass, Method method) {
		this.definedClass = definedClass;
		this.method = method;
		methodName = method.getName();
		Type type = method.getGenericReturnType();
		if (type instanceof ParameterizedType) {
			returnGenericType = (ParameterizedType) type;
			returnVoid = false;
		} else {
			returnType = (Class) type;
			returnVoid = (returnType == void.class);
		}
		parameters = method.getParameters();
	}

	public void setGetter(boolean getter) {
		this.getter = getter;
	}

	public void setSetter(boolean setter) {
		this.setter = setter;
	}

	public void setGetterSetterField(DefinedField getterSetterField) {
		this.getterSetterField = getterSetterField;
	}

	public DefinedClass getDefinedClass() {
		return definedClass;
	}

	public Method getMethod() {
		return method;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class getReturnType() {
		return returnType;
	}

	public ParameterizedType getReturnGenericType() {
		return returnGenericType;
	}

	public Parameter[] getParameters() {
		return parameters;
	}

	public boolean isGetter() {
		return getter;
	}

	public boolean isSetter() {
		return setter;
	}

	public DefinedField getGetterSetterField() {
		return getterSetterField;
	}

	public boolean isReturnVoid() {
		return returnVoid;
	}

	public<T extends Annotation> T getAnnotations(Class<T> annotation) {
		return method.getDeclaredAnnotation(annotation);
	}

	public boolean hasAnnotations(Class annotation) {
		return method.getDeclaredAnnotation(annotation) != null;
	}

}
