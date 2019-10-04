package com.yc.core;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
/**
 * 自定义加载器
 * 通过此类 对请求表单中地址对应到注解 找到相应的servlet
 * @author LQ
 *
 */
public class FileAnnoScanClassLoad extends ClassLoader {

	public FileAnnoScanClassLoad() {}
	//本地路径表示用“\”表示母文件夹与子文件夹的层次关系
	 // “/”则用来分隔网站的主机服务器等信息   非本地的路径分隔
	/**
	 * 根据包名找到使用了对应注解的所有类的集合
	 * @param packageName   包名
	 * @param annotation	注解
	 * @return				使用相关注解的类 的集合
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public List<Class<?>> getPackageAnno(String packageName,Class<? extends Annotation> annotation) throws ClassNotFoundException {
		String packageDirName = packageName.replace('.', '/');
		
		//枚举
		Enumeration<URL> dirs = null;
		try {
			//Thread.currentThread().getContextClassLoader().getResources(packageDirName)  线程上下文加载器  
			//来获取项目的根目录读取一下配置文件
			dirs = FileAnnoScanClassLoad.getSystemResources(packageDirName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(dirs.hasMoreElements()) {//是否还有下一个值
			//获取资源定位
			URL url = dirs.nextElement();
			//获取资源的协议
			String protocol = url.getProtocol();
			if(protocol.equals("file")) {
				//得到这 URL文件名
				String filePath = null;
				try {
					filePath = URLDecoder.decode(url.getFile(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				filePath = filePath.substring(1);//   /G:/workspace1/demo01/bin/com/yc/test -->  G:/workspace1/demo01/bin/com/yc/test
				return findClz(filePath,packageName,annotation);
			}
		}
		return null;
	}
	
	/**
	 * 找使用对应注解的类的集合
	 * @param filePath G:/workspace1/demo01/bin/com/yc/test 包路径
	 * @param packageName  包名
	 * @param annotation	注解
	 * @return
	 * @throws ClassNotFoundException
	 */
	private  List<Class<?>> findClz(String filePath,String packageName,Class<? extends Annotation> annotation) throws ClassNotFoundException {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		Path dir = Paths.get(filePath);
		DirectoryStream<Path> stream = null;
		try {
			//获取到Directory流  获取该路径下的所有文件目录
			stream = Files.newDirectoryStream(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(Path path:stream) {
			String fileName = String.valueOf(path.getFileName());
			String className = fileName.substring(0,fileName.length()-6);
			String filePathName = packageName+"."+className;
			String ClassPathName = packageName+"/" + fileName;
			Class<?> clz = findClzz(filePathName,ClassPathName,annotation);	
			if(clz!=null) {
				classList.add(clz);
			}
			
		}
		return classList;
	}
	/**
	 * 找到对应注解的class集合
	 * @param filePathName 
	 * @param ClassPathName
	 * @return
	 * @throws ClassNotFoundException
	 */
	private  Class<?> findClzz(String filePathName,String ClassPathName,Class<? extends Annotation> annotation) throws ClassNotFoundException{
		Class<?> clz = findLoadedClass(filePathName);
		if(clz == null) {
			ClassLoader loader = this.getParent();
			try {
				clz = loader.loadClass(filePathName);  //委托父加载器加载
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(clz == null) {
				byte[] classData = getClassData(ClassPathName);
				if(classData!=null) {
					//defineClass() 把字节码转化为Class
					clz = defineClass(filePathName, classData,0, classData.length);
				}else {
					throw new ClassNotFoundException();
				}
			}
		}
		// 判断该注解类型是不是所需要的类型
		if (null != clz && null != clz.getAnnotation(annotation)) {
			System.out.println("=========="+clz+"==============");
			// 把这个文件加入classlist中
			return clz;
		}
		return null;
	}
	
	private  byte[] getClassData(String path) {
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			is =new  FileInputStream(path);
			byte[] buffer = new byte[1024];
			int temp = 0;
			while((temp=is.read(buffer))!=-1) {
				baos.write(buffer, 0, temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(baos!=null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	 
	public static void main(String[] args) throws ClassNotFoundException {
		FileAnnoScanClassLoad loader = new FileAnnoScanClassLoad();
		List<Class<?>> ret = loader.getPackageAnno("com.yc.test", WebServlet.class);
		for(Class<?> clzName : ret) {
			try {
				Class clz = Class.forName(clzName.getName());
				WebServlet webServlet = (WebServlet) clz.getAnnotation(WebServlet.class);
				System.out.println(webServlet.value()[0]);
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
