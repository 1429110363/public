package com.yc.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;


public class Response {
	
	
	private BufferedWriter bw;
	//推送响应头信息
	private StringBuffer headInfo;
	//推送内容
	private StringBuffer content;
	private final String CRLF = "\r\n";
	private final String BLANK = " ";//""每空格 " "空格
	private int len;
	
	private SelectionKey key;
	private Selector selector;
	private ByteBuffer writeBuffer = ByteBuffer.allocate(1024*1024);
	
	public Response() {
		headInfo = new StringBuffer();
		content = new StringBuffer();
		len = 0;
	}
	
	public Response(SelectionKey key, Selector selector) {
		this();
		this.key = key ;
		this.selector = selector;
	}
	

	public Response print(String info) {
		content.append(info);
		len += info.toString().getBytes().length;
		return this;
	}
	public Response println(String info) {
		content.append(info).append(CRLF);
		len += (info+CRLF).toString().getBytes().length;
		return this;
	}
	/**
	 * 推送
	 * @param code
	 * @throws IOException
	 */
	public void push(int code) throws IOException {
		write(key,selector,code);
	}
	// 通道可写时  数据处理
	private void write(SelectionKey key, Selector selector,int code) {
		writeBuffer.clear();
		SocketChannel channel = (SocketChannel)key.channel();
		if(headInfo == null) {
			code = 500;
		}
		createHeadInfo(code);
		headInfo.append(content);
		try {
			// 将响应报文Buffer中。 写入的数据是一个字节数组。
			writeBuffer.put(headInfo.toString().getBytes());
			writeBuffer.flip();
			channel.write(writeBuffer);
			// 可读
			channel.register(this.selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据请求响应不同的状态码
	 * 响应报文
	 * @param code
	 */
	private void createHeadInfo(int code) {
		headInfo.append("HTTP/1.1").append(BLANK);
		headInfo.append(code).append(BLANK);
		switch(code) {
		 case 200:
			 headInfo.append("OK").append(CRLF);
			 break;
		 case 404:
			 headInfo.append("FOUND DEFAULT").append(CRLF);
			 break;
		 case 505:
			 headInfo.append("SERVER EORROR").append(CRLF);
			 break;
		}
		headInfo.append("Date:").append(new Date()).append(CRLF);
		headInfo.append("Server:").append("shsxt Server/0.0.1;charset=GBK").append(CRLF);
		headInfo.append("Content-type:text/html").append(CRLF);
		headInfo.append("Content-length:").append(len).append(CRLF);
		headInfo.append(CRLF);
	}
}
