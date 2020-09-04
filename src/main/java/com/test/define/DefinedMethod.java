package com.test.define;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.List;

public class DefinedMethod {
	private DefinedClass definedClass;
	private Method method;
	private String methodName;
	private Class returnType;
	private ParameterizedType returnGenericType;
	private TypeVariable[] returnVariableTypes;
	private Integer[] typeVariableIndexes;
	private Class[] typeVariableDefault;
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
			ParameterizedType parameterizedType = (ParameterizedType) type;
			returnGenericType = parameterizedType;
			returnType = (Class) parameterizedType.getRawType();
			Type[] types = parameterizedType.getActualTypeArguments();
			typeVariableDefault = new Class[types.length];
			returnVariableTypes = new TypeVariable[types.length];
			for (int i = 0; i < types.length; i++) {
				if (types[i] instanceof Class) {
					typeVariableDefault[i] = (Class) types[i];
				} else if (types[i] instanceof TypeVariable) {
					returnVariableTypes[i] = (TypeVariable) types[i];
					typeVariableDefault[i] = (Class) returnVariableTypes[i].getBounds()[0];
				}
			}
			returnVoid = false;
		} else if (type instanceof TypeVariable) {
			TypeVariable typeVariable = (TypeVariable) type;
			returnType = (Class) typeVariable.getBounds()[0];
			returnVariableTypes = new TypeVariable[]{typeVariable};
			typeVariableDefault = new Class[]{(Class) typeVariable.getBounds()[0]};
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

	public TypeVariable[] getReturnVariableTypes() {
		return returnVariableTypes;
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

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("\"definedClass\": ")
				.append('\"').append(definedClass.getClazz().getSimpleName()).append('\"');
		sb.append(", \"methodName\": ")
				.append('\"').append(methodName).append('\"');
		sb.append('}');
		return sb.toString();
	}
}
