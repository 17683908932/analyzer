package com.test.define;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class DefinedClass {
	private Class clazz;

	private List<DefinedField> fields;

	private List<DefinedMethod> methods;

	private Annotation[] annotations;

	public DefinedClass(Class clazz, boolean ignoreFields, boolean ignoreMethods) {
		this.clazz = clazz;
		if (!ignoreMethods) {
			setDefinedMethod();
		}
		if (!ignoreFields) {
			setDefinedField();
		}
		annotations = clazz.getAnnotations();
	}

	protected final void setDefinedMethod() {
		Method[] methodArray = clazz.getDeclaredMethods();
		List<DefinedMethod> definedMethods = new ArrayList<>(methodArray.length);
		for (int i = 0; i < methodArray.length; i++) {
			definedMethods.add(new DefinedMethod(this, methodArray[i]));
		}
		methods = definedMethods;
	}

	protected final void setDefinedField() {
		Field[] fieldArray = clazz.getDeclaredFields();
		List<DefinedField> definedFields = new ArrayList<>(fieldArray.length);
		for (int i = 0; i < fieldArray.length; i++) {
			definedFields.add(new DefinedField(this, fieldArray[i]));
		}
		fields = definedFields;
	}

	public Class getClazz() {
		return clazz;
	}

	public List<DefinedField> getFields() {
		return fields;
	}

	public List<DefinedMethod> getMethods() {
		return methods;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}
}
