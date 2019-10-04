package com.yc.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Scanner {
	/**
	 * 获取当前包路径下指定的Controller注解类型的文件
	 * 
	 * @param packageName 包名
	 * @param annotation  注解类型
	 * @return 文件
	 */
	public static List<Class<?>> getPackageController(String packageName, Class<? extends Annotation> annotation) {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs = null;
		// 获取当前目录下面的所有的子目录的url
		try {
			//getContextClassLoader() 返回该线程的上下文类加载器
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			
			// 得到这 URL协议名称     URL资源定位符   可能是后缀或者什么
			String protocol = url.getProtocol();

			// 如果当前类型是文件类型
			if ("file".equals(protocol)) {
				// 获取包的物理路径
				String filePath = null;
				try {
					filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					//System.out.println("url.getFile()------------->"+url.getFile());///G:/workspace1/demo01/bin/com/yc/test
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				filePath = filePath.substring(1);
				getFilePathClasses(packageName, filePath, classList, annotation);
			}
		}

		return classList;
	}

	/**
	 * 从指定的包下面找到文件名
	 * 
	 * @param packageName
	 * @param filePath
	 * @param classList
	 * @param annotation  注解类型
	 */
	private static List<Class<?>>  getFilePathClasses(String packageName, String filePath, List<Class<?>> classList,
			Class<? extends Annotation> annotation) {
		Path dir = Paths.get(filePath);
		//System.out.println("Paths.get(filePath)---->"+dir);//G:\workspace1\demo01\bin\com\yc\test
		DirectoryStream<Path> stream = null;
		try {
			// 获得当前目录下的文件的stream流
			stream = Files.newDirectoryStream(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Path path : stream) {
			String fileName = String.valueOf(path.getFileName());
			System.out.println("fileName--------------->"+fileName);//AnnoManageUtil.class
			String className = fileName.substring(0, fileName.length() - 6);//AnnoManageUtil
			System.out.println("className--------------->"+className);
			Class<?> classes = null;
			try {
				//System.out.println("packageName==============="+packageName);
				classes = Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			// 判断该注解类型是不是所需要的类型
			if (null != classes && null != classes.getAnnotation(annotation)) {
				//System.out.println("=========="+classes+"==============");
				// 把这个文件加入classlist中
				classList.add(classes);
			}
		}
		return classList;
	}

	
}
