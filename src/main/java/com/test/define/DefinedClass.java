package com.test.define;

import com.test.utils.CollectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.*;

@SuppressWarnings("all")
public class DefinedClass {
	private final static Map<Class, DefinedClass> cacheDefined = new HashMap<>();

	private Class clazz;
	//如果有 泛型数组
	private TypeVariable[] typeVariables;

	private Map<Field, DefinedField> fields;

	private Map<Method, DefinedMethod> methods;

	public static DefinedClass getDefinedClazz(Class clazz) {
		DefinedClass definedClass = cacheDefined.get(clazz);
		if (definedClass == null) {
			cacheDefined.put(clazz, (definedClass = new DefinedClass(clazz)));
		}
		return definedClass;
	}

	public DefinedClass(Class clazz) {
		this.clazz = clazz;

		typeVariables = clazz.getTypeParameters();
		if (typeVariables == null || typeVariables.length == 0) {
			typeVariables = null;
		}

		setDefinedMethod();
		setDefinedField();
		unionMethodField();
	}

	protected final void setDefinedMethod() {
		Method[] methodArray = clazz.getDeclaredMethods();
		Map<Method, DefinedMethod> definedMethods = new HashMap<>(methodArray.length);
		for (int i = 0; i < methodArray.length; i++) {
			definedMethods.put(methodArray[i], new DefinedMethod(this, methodArray[i]));
		}
		methods = definedMethods;
	}

	protected final void setDefinedField() {
		Field[] fieldArray = clazz.getDeclaredFields();
		Map<Field, DefinedField> definedFields = new HashMap<>(fieldArray.length);
		for (int i = 0; i < fieldArray.length; i++) {
			definedFields.put(fieldArray[i], new DefinedField(this, fieldArray[i]));
		}
		fields = definedFields;
	}

	public Class getClazz() {
		return clazz;
	}

	public Map<Field, DefinedField> getFields() {
		if (fields == null) { return null; }
		return Collections.unmodifiableMap(fields);
	}

	public Map<Method, DefinedMethod> getMethods() {
		if (methods == null) { return null; }
		return Collections.unmodifiableMap(methods);
	}

	public TypeVariable[] getTypeVariables() {
		return typeVariables;
	}

	public boolean isGenericClass() {
		return !CollectionUtil.isEmpty(typeVariables);
	}

	public<T extends Annotation> T getAnnotations(Class<T> annotation) {
		return (T) clazz.getDeclaredAnnotation(annotation);
	}

	public boolean hasAnnotations(Class annotation) {
		return clazz.getDeclaredAnnotation(annotation) != null;
	}

	private void unionMethodField() {
		if (CollectionUtil.isEmpty(methods) || CollectionUtil.isEmpty(fields)) { return; }
		for (Map.Entry<Field, DefinedField> fieldEntry : fields.entrySet()) {
			DefinedField definedField = fieldEntry.getValue();
			Method getter = definedField.getGetter();
			Method setter = definedField.getSetter();
			DefinedMethod definedGetterMethod, definedSetterMethod;
			if (getter != null && (definedGetterMethod = methods.get(getter)) != null) {
				definedGetterMethod.setGetter(true);
				definedGetterMethod.setGetterSetterField(definedField);
				definedField.setGetterMethod(definedGetterMethod);
			}
			if (setter != null && (definedSetterMethod = methods.get(setter)) != null) {
				definedSetterMethod.setSetter(true);
				definedSetterMethod.setGetterSetterField(definedField);
				definedField.setSetterMethod(definedSetterMethod);
			}
		}
	}
}
