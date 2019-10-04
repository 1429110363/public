package com.yc.core;

public interface Servlet {
	void service(Request request,Response response);
	void doGet(Request request,Response response);
	void doPost(Request request,Response response);
}
