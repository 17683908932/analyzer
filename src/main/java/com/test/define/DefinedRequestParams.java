package com.test.define;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.utils.CollectionUtil;
import com.test.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DefinedRequestParams {
	private String param;
	private List<DefinedRequestParams> paramValues;
	private boolean array = false;

	public boolean isArray() {
		return array;
	}

	public void setArray(boolean array) {
		this.array = array;
	}

	public String getParam() {
		return param;
	}

	public List<DefinedRequestParams> getParamValues() {
		return Collections.unmodifiableList(paramValues);
	}

	public void addParamValue(DefinedRequestParams definedRequestParam) {
		if (CollectionUtil.isEmpty(paramValues)) {
			paramValues = new ArrayList<>();
		}
		paramValues.add(definedRequestParam);
	}

	public void setParam(String param) {
		this.param = param;
	}

	public void setParamValues(List<DefinedRequestParams> paramValues) {
		this.paramValues = paramValues;
	}

	/**
	 * public object get(@requestBody string name)
	 * json格式参数: {name: ''}
	 *
	 * class XXXDto{
	 *     string dtoName;
	 *     int id;
	 * }
	 * public object get(@requestBody XXXDto name)
	 * json格式参数: {"dtoName":"", "id": ""}
	 * 不需要再有那个name了;
	 *
	 * @param params
	 * @return json格式的字符串
	 */
	public static String parseJsonString(DefinedRequestParams params) {
		JSONObject jsonObject = new JSONObject();
		JSON returned = jsonObject;
		if (CollectionUtil.isEmpty(params.paramValues)) {
			parseJsonString(params, jsonObject);
			return returned.toJSONString();
		}

		if (params.array) {
			JSONArray array = new JSONArray();
			returned = array;
			array.add(jsonObject);
		}
		for (DefinedRequestParams paramValue : params.paramValues) {
			parseJsonString(paramValue, jsonObject);
		}
		return returned.toJSONString();
	}

	public static String parseFormString(List<DefinedRequestParams> params) {
		DefinedRequestParams parent = new DefinedRequestParams();
		parent.paramValues = params;
		parent.param = "null";
		return parseFormString(parent);
	}

	public static String parseFormString(DefinedRequestParams params) {
		StringBuilder builder = new StringBuilder();
		if (params.array) {
			System.out.println("error: form-data not allow array");
		}
		if (CollectionUtil.isEmpty(params.paramValues)) {
			parseFormString(params, builder, null);
			return builder.toString();
		}
		Iterator<DefinedRequestParams> iter = params.paramValues.iterator();
		while (iter.hasNext()) {
			DefinedRequestParams paramValue = iter.next();
			if (CollectionUtil.isEmpty(paramValue.paramValues)) {
				parseFormString(paramValue, builder, null);
			} else {
				Iterator<DefinedRequestParams> childIter = paramValue.paramValues.iterator();
				while (childIter.hasNext()) {
					parseFormString(childIter.next(), builder, paramValue.param);
				}
			}
		}
		builder.deleteCharAt(builder.length()-1);
		return builder.toString();
	}

	private static void parseFormString(DefinedRequestParams params, StringBuilder builder, String parentName) {
		append(builder, params.param, parentName).append('=').append('&');
	}

	private static void parseJsonString(DefinedRequestParams params, JSONObject json) {
		if (CollectionUtil.isEmpty(params.paramValues)) {
			if (params.array) {
				json.put(params.param, new Object[0]);
			} else {
				json.put(params.param, null);
			}
			return ;
		}
		JSONObject jsonObject = new JSONObject();
		if (params.array) {
			JSONArray array = new JSONArray();
			array.add(jsonObject);
			json.put(params.param, array);
		} else {
			json.put(params.param, jsonObject);
		}
		for (DefinedRequestParams paramValue : params.paramValues) {
			parseJsonString(paramValue, jsonObject);
		}
	}

	private static StringBuilder append(StringBuilder builder, String name, CharSequence parentName) {
		if (StringUtil.isEmpty(parentName)) {
			builder.append(name);
		} else {
			builder.append(parentName).append('.').append(name);
		}
		return builder;
	}

}
