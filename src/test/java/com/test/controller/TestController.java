package com.test.controller;

import com.test.common.RequestParams;
import com.test.domain.UserDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
public class TestController {

	@RequestMapping("default/addUser")
	public Object test(UserDto userDto) {
		return "code";
	}

	@RequestMapping("post/addUser")
	public Object test(@RequestBody RequestParams<UserDto> params) {
		return "code";
	}

	@GetMapping("default/getUser")
	public Object test(Integer id, String name) {
		return "code";
	}

	@GetMapping("default/getUser/{id}")
	public Object testPath(@PathVariable("id") Integer id, String name) {
		return "code";
	}

	@GetMapping("default/get")
	public Object testParam(@PathVariable("id") Integer id, @RequestParam("nickName") String name) {
		return "code";
	}

}
