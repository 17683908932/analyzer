package com.test.domain;

import java.util.List;

public class UserDto {
	private String userName;
	private Integer ages;
	private List<String> nickNames;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getAges() {
		return ages;
	}

	public void setAges(Integer ages) {
		this.ages = ages;
	}

	public List<String> getNickNames() {
		return nickNames;
	}

	public void setNickNames(List<String> nickNames) {
		this.nickNames = nickNames;
	}
}
