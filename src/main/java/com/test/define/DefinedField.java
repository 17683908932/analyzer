package com.test.define;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("all")
public class DefinedField {

	private DefinedClass definedClass;

	private Field field;

	private String fieldName;

	private Class fieldClass;

	private DefinedMethod getterMethod;

	private DefinedMethod setterMethod;

	private Annotation[] annotations;

	public DefinedField(DefinedClass definedClass, Field field) {
		if (definedClass == null || field == null) {
			throw new NullPointerException("definedClass or field is null");
		}
		this.definedClass = definedClass;
		this.field = field;

		this.fieldName = field.getName();
		this.fieldClass = field.getDeclaringClass();
		this.annotations = field.getDeclaredAnnotations();
		setGetterMethod();
		setSetterMethod();
	}

	public String getFieldName() {
		return fieldName;
	}

	public DefinedClass getDefinedClass() {
		return definedClass;
	}

	public Field getField() {
		return field;
	}

	public Class getFieldClass() {
		return fieldClass;
	}

	public DefinedMethod getGetter() {
		return getterMethod;
	}

	public DefinedMethod getSetter() {
		return setterMethod;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	private void setGetterMethod() {
		String getterMethodName;
		if (fieldName.length() > 1) {
			getterMethodName = "get"+Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
		} else {
			getterMethodName = "get"+Character.toUpperCase(fieldName.charAt(0));
		}
		if (definedClass.getMethods() == null) {
			try {
				Method method = definedClass.getClazz().getDeclaredMethod(getterMethodName, null);
				getterMethod = new DefinedMethod(definedClass, method);
			} catch (NoSuchMethodException e) {}
		} else {
			for (DefinedMethod definedMethod : definedClass.getMethods()) {
				if (definedMethod.getMethodName().equals(getterMethodName)) {
					getterMethod = definedMethod;
					return;
				}
			}
		}

	}

	private void setSetterMethod() {
		String setterMethodName;
		if (fieldName.length() > 1) {
			setterMethodName = "set"+Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
		} else {
			setterMethodName = "set"+Character.toUpperCase(fieldName.charAt(0));
		}
		if (definedClass.getMethods() == null) {
			try {
				Method method = definedClass.getClazz().getDeclaredMethod(setterMethodName, fieldClass);
				setterMethod = new DefinedMethod(definedClass, method);
			} catch (NoSuchMethodException e) {}
		} else {
			for (DefinedMethod definedMethod : definedClass.getMethods()) {
				if (definedMethod.getMethodName().equals(setterMethodName)) {
					setterMethod = definedMethod;
					return;
				}
			}
		}

	}
}
