package com.flipchat.server;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipchat.server.initializer.StringChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class FlipChatServer {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) {
		try {
			int port = Integer.parseInt(args[0]);
			new FlipChatServer().run(port);
		} catch (Exception e) {
			System.err.println("it needs a port parameter");
		}
	}
	
	public void run(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.TRACE))
					.childHandler(new StringChannelInitializer());
			
			Channel channel = bootstrap.bind(port).sync().channel();
			
			channel.closeFuture().sync();
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}
