package com.test.define;

import com.test.enums.GenericTypeEnum;
import com.test.utils.CollectionUtil;

import static com.test.enums.GenericTypeEnum.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

@SuppressWarnings("all")
public class DefinedField {

	private DefinedClass definedClass;

	private Field field;

	private String fieldName;

	//非泛型
	private Class fieldClass;
	//泛型类型T 或 E这种
	private TypeVariable[] fieldTypeVariables;
	//对应类里面的泛型数组的位置
	private Integer[] genericTypeIndexes;
	//泛型 Map<K, V>
	private ParameterizedType fieldParameterizedType;
	//类型
	private GenericTypeEnum genericType = type_Class;

	private DefinedMethod getterMethod;
	private Method getter;

	private DefinedMethod setterMethod;
	private Method setter;

	public DefinedField(DefinedClass definedClass, Field field) {
		if (definedClass == null || field == null) {
			throw new NullPointerException("definedClass or field is null");
		}
		this.definedClass = definedClass;
		this.field = field;

		this.fieldName = field.getName();
		Type type = field.getGenericType();
		if (type instanceof TypeVariable) {
			TypeVariable typeVariable = (TypeVariable) type;
			genericType = type_TypeVariable;
			fieldTypeVariables = new TypeVariable[]{typeVariable};
			fieldClass = (Class) typeVariable.getBounds()[0];
		} else if (type instanceof ParameterizedType) {
			genericType = type_ParameterizedType;
			fieldParameterizedType = (ParameterizedType) type;
			fieldClass = (Class) fieldParameterizedType.getRawType();
			Type[] types = fieldParameterizedType.getActualTypeArguments();
			fieldTypeVariables = new TypeVariable[types.length];
			for (int i = 0; i < types.length; i++) {
				fieldTypeVariables[i] = (TypeVariable) (types[i]);
			}
		} else if (type instanceof Class){
			fieldClass = (Class) type;
		}
		markTypeVariableIndex();
		setGetterMethod();
		setSetterMethod();
	}

	public DefinedClass getDefinedClass() {
		return definedClass;
	}

	public Field getField() {
		return field;
	}

	public GenericTypeEnum getGenericType() {
		return genericType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Class getFieldClass() {
		return fieldClass;
	}

	public ParameterizedType getFieldGenericType() {
		return fieldParameterizedType;
	}

	public Integer[] getGenericTypeIndexes() {
		return genericTypeIndexes;
	}

	public void setGetterMethod(DefinedMethod getterMethod) {
		this.getterMethod = getterMethod;
	}

	public void setSetterMethod(DefinedMethod setterMethod) {
		this.setterMethod = setterMethod;
	}

	public DefinedMethod getGetterMethod() {
		return getterMethod;
	}

	public Method getGetter() {
		return getter;
	}

	public DefinedMethod getSetterMethod() {
		return setterMethod;
	}

	public Method getSetter() {
		return setter;
	}

	public<T extends Annotation> T getAnnotations(Class<T> annotation) {
		return field.getDeclaredAnnotation(annotation);
	}

	public boolean hasAnnotations(Class annotation) {
		return field.getDeclaredAnnotation(annotation) != null;
	}

	private void markTypeVariableIndex() {
		TypeVariable[] classTypeVariables = definedClass.getTypeVariables();

		if (CollectionUtil.isEmpty(classTypeVariables)
				|| CollectionUtil.isEmpty(fieldTypeVariables)) { return; }

		genericTypeIndexes = new Integer[fieldTypeVariables.length];
		for (int i = 0; i < fieldTypeVariables.length; i++) {
			TypeVariable typeVariable = fieldTypeVariables[i];
			for (int ci = 0; ci < classTypeVariables.length; ci++) {
				if (typeVariable == classTypeVariables[ci]) {
					genericTypeIndexes[i] = ci;
				}
			}
		}
	}

	private void setGetterMethod() {
		String getterMethodName;
		if (fieldName.length() > 1) {
			getterMethodName = "get"+Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
		} else {
			getterMethodName = "get"+Character.toUpperCase(fieldName.charAt(0));
		}
		try {
			getter = definedClass.getClazz().getDeclaredMethod(getterMethodName, null);
		} catch (NoSuchMethodException e) {}

	}

	private void setSetterMethod() {
		String setterMethodName;
		if (fieldName.length() > 1) {
			setterMethodName = "set"+Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
		} else {
			setterMethodName = "set"+Character.toUpperCase(fieldName.charAt(0));
		}
		try {
			setter = definedClass.getClazz().getDeclaredMethod(setterMethodName, fieldClass == null? Object.class: fieldClass);
		} catch (NoSuchMethodException e) {}

	}
}
