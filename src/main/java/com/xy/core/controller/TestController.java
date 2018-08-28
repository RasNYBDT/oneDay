package com.xy.core.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xy.annotation.MyController;
import com.xy.annotation.MyRequestMapping;
import com.xy.annotation.MyRequestParam;

@MyController
public class TestController {

	@MyRequestMapping("/my/doTest1")
	public void test1(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("param") String param) {
		System.out.println(param);
		try {
			response.getWriter().write("doTest method success! param:" + param);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@MyRequestMapping("/my/doTest2")
	public void test2(HttpServletRequest request, HttpServletResponse response){
		try {
			response.getWriter().println("doTest2 method success!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
