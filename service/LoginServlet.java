package com.yc.service;

import com.yc.core.HttpServlet;
import com.yc.core.Request;
import com.yc.core.Response;
import com.yc.core.WebServlet;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	public LoginServlet() {
		super();
	}

	@Override
	public void doGet(Request request,Response response) {
		response.print("<html>");
		response.print("<head>");
		response.print("<title>");
		response.print("</title>");
		response.print("</head>");
		response.print("<body>");
		response.print("hello word"+request.getParamter("name"));
		response.print("</body>");
		response.print("</html>");
	}

	@Override
	public void doPost(Request request,Response response) {
		doGet(request,response);
	}

}
