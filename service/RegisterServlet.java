package com.yc.service;

import com.yc.core.HttpServlet;
import com.yc.core.Request;
import com.yc.core.Response;
import com.yc.core.Servlet;
import com.yc.core.WebServlet;

@WebServlet("/reg")
public class RegisterServlet extends HttpServlet {

	@Override
	public void service(Request request, Response response) {
		response.print("注册成功");
	}


	@Override
	public void doGet(Request request, Response response) {
		
	}

	@Override
	public void doPost(Request request, Response response) {
		doGet(request,response);
	}

}
