package com.test.params;

import com.alibaba.fastjson.JSONObject;
import com.test.constants.ClassSign;
import com.test.define.DefinedClass;
import com.test.define.DefinedField;
import com.test.define.DefinedMethod;
import com.test.define.DefinedRequestParams;
import com.test.utils.CollectionUtil;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.*;
import java.util.*;

public class RequestParamsParser {
	private Map<String, Object> EMPTY_MAP = Collections.EMPTY_MAP;
	private LocalVariableTableParameterNameDiscoverer variableTableDiscover;

	public RequestParamsParser() {
		this.variableTableDiscover = new LocalVariableTableParameterNameDiscoverer();
	}

	public List<DefinedRequestParams> methodParseParams(DefinedMethod requestMethod) {
		Parameter[] parameters = requestMethod.getParameters();
		List<DefinedRequestParams> requestParams = new ArrayList<>();

		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			if (parameter.getAnnotation(PathVariable.class) != null) {
				continue;
			}

			String name = null;
			RequestParam requestParam = parameter.getDeclaredAnnotation(RequestParam.class);
			if (requestParam != null) {
				name = requestParam.value().length() == 0?requestParam.name():requestParam.value();
			} else {
				name = variableTableDiscover.getParameterNames(requestMethod.getMethod())[i];
			}
			Type type = parameter.getParameterizedType();
			DefinedRequestParams definedRequestParam;
			if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Type[] types = parameterizedType.getActualTypeArguments();
				Class[] genericClasses = new Class[types.length];
				for (int ti = 0; ti < types.length; ti++) {
					genericClasses[ti] = (Class) types[ti];
				}
				definedRequestParam = parseParameterName(null, (Class) parameterizedType.getRawType(), name, genericClasses);
			}  else {
				definedRequestParam = parseParameterName(null, (Class) type, name, null);
			}
			requestParams.add(definedRequestParam);
		}
		return requestParams;
	}

	/**
	 *
	 * @param parent
	 * 可以为空, 外层参数
	 * @param type
	 * 不可为空 被解析的目标类型
	 * @param defaultName
	 * 默认名称 不可为空
	 * @param genericTypes
	 * 泛型类型
	 * @return
	 * @throws Exception
	 */
	public DefinedRequestParams parseParameterName(DefinedRequestParams parent, Class type, String defaultName, Class[] genericTypes) {
		DefinedRequestParams requestParams = new DefinedRequestParams();
		requestParams.setParam(defaultName);
		if (parent != null) {
			parent.addParamValue(requestParams);
		}
		if (parent == null) {
			parent = requestParams;
		}

		Class actualClass = type;
		if (ClassSign.isArray(type)) {
			actualClass = type.getComponentType();
			requestParams.setArray(true);
		}
		if (ClassSign.isCollection(type)) {
			if (CollectionUtil.isEmpty(genericTypes)) {
				actualClass = Object.class;
			} else {
				actualClass = genericTypes[0];
			}
			requestParams.setArray(true);
		}

		if (ClassSign.needDepth(actualClass)) {
			DefinedClass definedClass = DefinedClass.getDefinedClazz(actualClass);
			Map<Field, DefinedField> fieldMap = definedClass.getFields();
			if (CollectionUtil.isEmpty(fieldMap)) { return parent; }

			for (Map.Entry<Field, DefinedField> fieldEntry : fieldMap.entrySet()) {
				DefinedField definedField = fieldEntry.getValue();
				if (definedField.getSetterMethod() != null) {
					switch (definedField.getGenericType()) {
						case type_Class: {
							parseParameterName(requestParams, definedField.getFieldClass(), definedField.getFieldName(), null);
						} break;
						case type_ParameterizedType: {
							parseParameterNameGeneric(requestParams, definedField, definedClass, genericTypes);
						} break;
						case type_WildcardType: {
							System.out.println("class: "+actualClass.getSimpleName()+" field:"+definedField.getFieldName());
						} break;
						case type_GenericArrayType: {
							System.out.println("class: "+actualClass.getSimpleName()+" field:"+definedField.getFieldName());
						} break;
						case type_TypeVariable: {
							parseParameterNameGeneric(requestParams, definedField, definedClass, genericTypes);
						};
						default: {
							System.out.println("error: GenericType::"+definedField.getGenericType());
						} break;
					}
				}
			}
		}
		return parent;
	}

	private void parseParameterNameGeneric(DefinedRequestParams requestParams, DefinedField definedField, DefinedClass definedClass, Class[] genericTypes) {
		Class[] fieldTypes = null;
		if (!CollectionUtil.isEmpty(genericTypes) && !CollectionUtil.isEmpty(definedField.getGenericTypeIndexes())) {
			int actualTypes = 0, classFileTypes = 0;
			Integer[] indexes = definedField.getGenericTypeIndexes();
			if ((classFileTypes = indexes.length) > (actualTypes = genericTypes.length)) {
				System.out.println("may error: fieldName: "+definedField.getFieldName() + " Class: "+definedClass.getClazz().getSimpleName()+" [actualTypes,classFileTypes]: "+classFileTypes+", "+ actualTypes);
			}
			fieldTypes = new Class[Math.min(actualTypes, classFileTypes)];
			for (int i = 0; i < indexes.length; i++) {
				Integer index = indexes[i];
				if (index < actualTypes) {
					fieldTypes[i] = genericTypes[index];
				}
			}
		}
		parseParameterName(requestParams, definedField.getFieldClass(), definedField.getFieldName(), fieldTypes);
	}
}
