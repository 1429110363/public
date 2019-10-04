package com.yc.core;


public abstract class HttpServlet implements Servlet {
	public HttpServlet(){
		
	}
	@Override
	public void service(Request request, Response response) {
		if(request.getUrl().equals("get")) {
			doGet(request,response);
		}else {
			doPost(request,response);
		}
	}

	@Override
	public void doGet(Request request, Response response) {
		
	}

	@Override
	public void doPost(Request request, Response response) {
		doGet(request,response);
	}

}
