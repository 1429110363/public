package com.yc.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerStart {
	private boolean isRunning = false;
	// 多路复用器， 选择器。 用于注册通道的。
	// Selector选择器类管理着一个被注册的通道集合的信息和它们的就绪状态。
	// 通道是和选择器一起被注册的，并且使用选择器来更新通道的就绪状态。当这么做的时候，可以选择将被激发的线程挂起，直到有就绪的的通道。
	private Selector selector;
	
	
	public static void main(String[] args) {
		ServerStart server = new ServerStart();
		server.start(8888);
	}
	
	
	
	// 开启服务
	public void start(int port) {
		try {
			// 开启多路复用器
			selector = Selector.open();
			// 注册服务器通道
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			// 设置为非阻塞通道
			serverChannel.configureBlocking(false);
			// 绑定端口
			serverChannel.bind(new InetSocketAddress(port));
			// 将该通道注册到多路复用器上，并设置通道的状态 OP_ACCEPT - 连接成功状态
			// OP_READ - 可读状态
			// OP_WRITE - 可写状态
			// OP_CONNECT - 通道建立成功
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("服务启动成功....");
			isRunning = true;
			receive();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("服务启动失败....");
		}
		
	};
	// 等待连接
	private void receive() {
		// 开启线程池
		ExecutorService service = Executors.newFixedThreadPool(20);
		service.execute(new Dispatcher(selector));
	}
	//关停服务
	private void stop() {
		isRunning = false;
		try {
			selector.close();
			System.out.println("服务器已关闭...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	};
	
}
