package com.test.define;

import java.util.Arrays;

public class DefinedRequestUrl implements Cloneable {
	private String url;
	private String[] method;
	private String[] firstDomain;
	private String[] secondDomain;
	private String params;

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("host: ").append(url).append("\r\n");
		sb.append(Arrays.toString(method)).append("    \r\n")
				.append(Arrays.toString(firstDomain)).append("\r\n")
				.append(Arrays.toString(secondDomain)).append("\r\n");
		sb.append("params: ").append(params);
		return sb.toString();
	}

	@Override
	public DefinedRequestUrl clone() {
		try {
			DefinedRequestUrl definedRequestUrl = (DefinedRequestUrl) super.clone();
			definedRequestUrl.method = this.method == null? null: this.method.clone();
			definedRequestUrl.firstDomain = this.firstDomain == null? null: this.firstDomain.clone();
			definedRequestUrl.secondDomain = this.secondDomain == null? null: this.secondDomain.clone();
			definedRequestUrl.defaultUrl();
			return definedRequestUrl;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	protected void defaultUrl() {
		if (method == null || method.length == 0) {
			method = new String[]{"GET"};
		}
		if (firstDomain == null || firstDomain.length == 0) {
			firstDomain = new String[]{""};
		}
		if (secondDomain == null || secondDomain.length == 0) {
			secondDomain = new String[]{""};
		}
	}

	public String[] toUrl() {
		Integer size = method.length * firstDomain.length * secondDomain.length;
		String[] allUrl = new String[size];
		for (int mi = 0; mi < method.length; mi++) {
			for (int fi = 0; fi < firstDomain.length; fi++) {
				for (int si = 0; si < secondDomain.length; si++) {
					StringBuilder builder = new StringBuilder()
							.append(getUrl()).append(getDomain(firstDomain[fi]))
							.append(getDomain(secondDomain[si])).append(' ')
							.append(method[mi]).append(' ').append(params);
					allUrl[--size] = builder.toString();
				}
			}

		}
		return allUrl;
	}

	private String getDomain(String str) {
		int start = 0, end = str.length() - 1;
		if (start >= end) { return ""; }
		if (str.charAt(0) == '/') {
			start = 1;
		}
		if (str.charAt(end) != '/') {
			str+='/';
			end+=1;
		}
		if (start >= end) { return ""; }
		str = str.substring(start);
		return str;
	}

	public String getUrl() {
		if (url.charAt(url.length()-1) != '/') {
			return url+'/';
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String[] getMethod() {
		return method;
	}

	public void setMethod(String[] method) {
		this.method = method;
	}

	public String[] getFirstDomain() {
		return firstDomain;
	}

	public void setFirstDomain(String[] firstDomain) {
		this.firstDomain = firstDomain;
	}

	public String[] getSecondDomain() {
		return secondDomain;
	}

	public void setSecondDomain(String[] secondDomain) {
		this.secondDomain = secondDomain;
	}

	public String getParams() {
		return params;
	}

	public void paramsAppend(String name) {
		if (params != null && params.length() > 0) {
			params = params+"&"+name+"=";
		} else {
			params = name+"=";
		}

	}

	public void setParams(String params) {
		this.params = params;
	}
}
