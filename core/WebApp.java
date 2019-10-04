package com.yc.core;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class WebApp {
	//private static WebContext webContext;
	// SAX解析web.xml 方式解决表单访问
//	static {
//		try { 
//			//SAX解析
//			//1、获取解析工厂
//			SAXParserFactory factory=SAXParserFactory.newInstance();
//			//2、从解析工厂获取解析器
//			SAXParser parse =factory.newSAXParser();
//			//3、编写处理器
//			//4、加载文档 Document 注册处理器
//			WebHandler handler=new WebHandler();
//			//5、解析
//			parse.parse(Thread.currentThread().getContextClassLoader()
//			.getResourceAsStream("web.xml")
//			,handler);
//			webContext = new WebContext(handler.getEntitys(), handler.getMappings());
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	/**
//	 * 根据url地址 获取不同的servlet 
//	 * 分发器实现关键一步
//	 * @param url
//	 * @return
//	 */
//	public static Servlet getServletFormUrl(String url) {
//		String className = WebContext.getClzz("/"+url);	
//		if(null!=className) {
//			try {
//				Class clazz = Class.forName(className);
//				return (Servlet) clazz.newInstance();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
}
