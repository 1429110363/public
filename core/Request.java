package com.yc.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
	
	
	//请求信息
	private String requestInfo;
	//请求地址
	private String url;
	//请求方法
	private String method;
	//请求参数
	private String queryStr;
	private final String CRLF = "\r\n";
	// 存储请求参数
	private Map<String,List<String>> paramterMap;
	
	// 缓存区  	pos - 游标位置,lim - 容器中的有效值 (默认值为buffer的容量), cap - 容量
	private ByteBuffer readBuffer = ByteBuffer.allocate(1024*1024);
	private Selector selector;
	private SelectionKey key;
	
	
	
	public Request(SelectionKey key,Selector selector){
		this.key = key ;
		this.selector = selector;
		init(key);
	}
	
	// 读取请求信息
	private void init(SelectionKey key) {
		paramterMap = new HashMap<String,List<String>>();
		try {
			// 缓存清空
			this.readBuffer.clear();
			// 获取当前与服务器关联的通道
			SocketChannel channel = (SocketChannel)key.channel();
			int readLength;
			// 读取缓存中数据
			readLength = channel.read(readBuffer);
			if(readLength == -1){
				// 关闭通道
				key.channel().close();
				// 关闭连接
				key.cancel();
				return;
			}
			// 重置游标  lim = pos ; pos = 0 ;
			this.readBuffer.flip();
			// readBuffer.remaining() 缓存中有效值得长度
			byte[] datas = new byte[readBuffer.remaining()];
			readBuffer.get(datas);
			requestInfo = new String(datas);
			System.out.println(new String(datas));
			parseHeadInfo();
			// 将通道标记为 可写
			channel.register(selector, SelectionKey.OP_WRITE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
	/**
	 * url
	 * method  post get
	 * 请求参数
	 * 分解请求信息
	 */
	private void parseHeadInfo() {
		this.method = this.requestInfo.substring(0,this.requestInfo.indexOf("/")).trim().toLowerCase();
		int startIndex = this.requestInfo.indexOf("/")+1;
		int endIndex = this.requestInfo.indexOf("HTTP/");
		this.url = this.requestInfo.substring(startIndex,endIndex).trim();
		int qIndex = this.url.indexOf("?");
		if(qIndex >= 0) {
			String[] qArray = this.url.split("\\?");
			this.url=qArray[0];
			queryStr=qArray[1];
		}
		
		if(method.equals("post")) {
			String qStr = this.requestInfo.substring(this.requestInfo.lastIndexOf(CRLF)).trim();
			if(queryStr!=null) {
				queryStr += "&" + qStr;
			}else {
				queryStr = qStr;
			}
		}
		queryStr = queryStr==null?"":queryStr;
		conventMap();
	}
	/**
	 * 解析请求参数并存储
	 * 
	 */
	private void conventMap() {
		String[] keyArray = queryStr.split("&");
		for(String query:keyArray) {
			String[] kv = query.split("=");
			kv = Arrays.copyOf(kv, 2);
			String key = kv[0];
			String value = kv[1]==null?null:decode(kv[1],"utf-8");
			if(!paramterMap.containsKey(key)) {
				paramterMap.put(key, new ArrayList<String>());
			}
			paramterMap.get(key).add(value);
		}
		
	}
	
	private String decode(String value,String decode) {
		try {
			return java.net.URLDecoder.decode(value, decode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	

	public String[] getParamterValues(String name) {
		List<String> values = paramterMap.get(name);
		if(values == null || values.size() <1) {
			return null;
		}
		return values.toArray(new String[0]);
	}
	
	
	public String getParamter(String name) {
		String[] values = getParamterValues(name);
		return values==null?null:values[0];
	}
	
	public String getUrl() {
		return url;
	}
	
	
	
}
