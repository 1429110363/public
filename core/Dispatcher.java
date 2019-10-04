package com.yc.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;



/**
 * 分发器   分发servlet
 * @author LQ
 *
 */
public class Dispatcher implements Runnable {
	private Request request;
	private Response response;
	
	private Selector selector;
	
	public Dispatcher(Selector selector) {
		this.selector = selector ;
	}	
	
	@Override
	public void run() {
		HttpServlet servlet = null;
		while(true) {
			try {
				// 多路复用器被选中
				selector.select();
				// 获取该多路复用器上的通道上的通道状态 相当于是通道的ID。 集合
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
				while(keys.hasNext()){
					SelectionKey key = keys.next();
					// 将本次要处理的通道从集合中删除，下次循环根据新的通道列表再次执行必要的业务逻辑
					keys.remove();
					if(key.isValid()) {
						// 阻塞状态
						try{
							if(key.isAcceptable()){
								accept(key,selector);
							}
						}catch(CancelledKeyException cke){
							// 断开连接。 出现异常。
							key.cancel();
						}
						// 可读状态
						try{
							if(key.isReadable()){
								// 将缓存中的信息保存到request中
								request = new Request(key,selector);
								// 通过表单地址找到对应servlet
								// servlet = (HttpServlet) WebApp.getServletFormUrl(request.getUrl());
								servlet = (HttpServlet) WebContext.getServletFormUrl(request.getUrl());
							}
						}catch(CancelledKeyException cke){
							key.cancel();
						}
						// 可写状态
						try{
							if(key.isWritable()){
								try {
									// 服务器响应信息
									response = new Response(key,selector);
									if(null!=servlet) {
										servlet.service(request, response);
										// 状态码
										response.push(200); 
									}else {
										response.push(404);
									}
								}catch(IOException e) {
									response.push(505);
								}
								
							}
						}catch(CancelledKeyException cke){
							key.cancel();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
		
	private void accept(SelectionKey key,Selector selector){
		try {
			// 此通道为init方法中注册到Selector上的ServerSocketChannel
			ServerSocketChannel serverChannel = (ServerSocketChannel)key.channel();
			// 阻塞方法，当客户端发起请求后返回。 此通道和客户端一一对应。
			SocketChannel channel = serverChannel.accept();
			channel.configureBlocking(false);
			// 设置对应客户端的通道标记状态，此通道为读取数据使用的。
			channel.register(selector, SelectionKey.OP_READ);
			
			System.out.println(channel+"客户端连接成功...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
//	private void release() {
//		try {
//			client.close();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//	}
}
