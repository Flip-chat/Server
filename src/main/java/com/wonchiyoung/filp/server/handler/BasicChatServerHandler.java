package com.wonchiyoung.filp.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class BasicChatServerHandler extends ChannelInboundHandlerAdapter {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		log.debug("handlerAdded of [SERVER] > {}", incoming.remoteAddress());
		
		for (Channel channel : CHANNEL_GROUP) {
			// 사용자가 추가되었을 때 기존 사용자에게 알림
			channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " has joined.\n");
		}
		
		CHANNEL_GROUP.add(incoming);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		// 사용자가 접속 했을 때 서버에 표시
		log.debug("User Access! > {}", incoming.remoteAddress());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String message = null;
		message = (String) msg;
		Channel incoming = ctx.channel();
		log.debug("channelRead of [SERVER] {} > {}", incoming.remoteAddress(), msg);
		
		for (Channel channel : CHANNEL_GROUP) {
			if (channel != incoming) {
				// 메시지 전달
				channel.writeAndFlush("[" + incoming.remoteAddress() + "] " + message + "\n");
			}
		}
		
		if ("exit".equals(message.toLowerCase())) {
			ctx.close();
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		log.debug("handlerRemoved of [SERVER] > {}", incoming.remoteAddress());
		
		for (Channel channel : CHANNEL_GROUP) {
			// 사용자가 나갔을 때 기존 사용자에게 알림
			channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " has left!\n");
		}
		
		CHANNEL_GROUP.remove(incoming);
	}

}
