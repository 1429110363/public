package com.yc.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 存储解析文件信息
 * @author LQ
 *
 */
public class WebContext {
//	private List<Entity> entitys = null;
//	private List<Mapping> mappings = null;;
//	private Entity entity;
//	private Mapping mapping;
//	private Map<String,String> entityMap = new HashMap<String,String>();
//	private Map<String,String> mappingMap = new HashMap<String,String>();
//	public WebContext(List<Entity> entitys, List<Mapping> mappings) {
//		this.entitys = entitys;
//		this.mappings = mappings;
//		
//		for(Entity entity: entitys) {
//			entityMap.put(entity.getName(), entity.getClazz());
//		}
//		//<url-pattern>可以多个 做键
//		for(Mapping mapping: mappings) {
//			for(String pattern:mapping.getPatterns()) {
//				mappingMap.put(pattern,mapping.getName());
//			}
//		}
//		
//	}
	
	/**
	 * 通过URL的路径找到了对应class（web.xml）
	 * @param pattern
	 * @return
	 */
//	public String getClz(String pattern) {
//		String name = mappingMap.get(pattern);
//		return entityMap.get(name);
//	}
	
	/**
	 * 根据url地址 获取不同的servlet 
	 * 分发器实现关键一步
	 * @param url
	 * @return
	 */
	public static Servlet getServletFormUrl(String url) {
		String className = WebContext.getClzz("/"+url);	
		if(null!=className) {
			try {
				Class clazz = Class.forName(className);
				return (Servlet) clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 通过URL的路径找到了对应class (注解)
	 * @param pattern
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public static String getClzz(String pattern)  {
		// 自定义类加载器
		FileAnnoScanClassLoad loader = new FileAnnoScanClassLoad();
		List<Class<?>> clazz = null;
		try {
			//获取到某包中某注解 所有类的集合
			clazz = loader.getPackageAnno("com.yc.service", WebServlet.class);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			System.out.println("404");
		}
		for(Class<?> clzName:clazz) {
			try {
				Class clz = Class.forName(clzName.getName());
				WebServlet webServlet = (WebServlet) clz.getAnnotation(WebServlet.class);
				for(String str:webServlet.value()) {
					if(pattern.equals(str)) {
						return clzName.getName();
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	

}
