package com.flipchat.server;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipchat.server.initializer.StringChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class FlipChatServer {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final int PORT;
	
	public FlipChatServer(int port) {
		this.PORT = port;
	}
	
	public static void main(String[] args) {
		System.out.println("Flip chat server is starting..");
		try {
			new FlipChatServer(8000).run();
		} catch (Exception e) {
			System.out.println("An error occurred while running the chat server.");
			e.printStackTrace();
		}
	}
	
	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.TRACE))
					.childHandler(new StringChannelInitializer());
			
			bootstrap.bind(PORT).sync().channel().closeFuture().sync();
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}
