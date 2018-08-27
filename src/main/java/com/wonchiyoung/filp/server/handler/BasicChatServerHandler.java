package com.wonchiyoung.filp.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wonchiyoung.filp.server.env.SupportDevice;
import com.wonchiyoung.filp.server.info.ChatRoom;
import com.wonchiyoung.filp.server.info.ChatUser;
import com.wonchiyoung.filp.server.manager.RoomManager;
import com.wonchiyoung.filp.server.manager.UserManager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class BasicChatServerHandler extends ChannelInboundHandlerAdapter {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		String userKey = incoming.id().toString();
		log.debug("Handler Added of [SERVER] {}[{} / {}]", userKey, incoming.remoteAddress(), SupportDevice.WEB);
		
		// 채팅방 없으면 생성
		RoomManager roomManager = RoomManager.getInstance();
		if (!roomManager.isExistsRoom("1")) {
			ChatRoom chatRoom = new ChatRoom("1");
			roomManager.addRoom("1", chatRoom);
		}
		
		ChatRoom chatRoom = roomManager.getRoom("1");
		
		UserManager userManager = UserManager.getInstance();
		
		for (String key : chatRoom.getUsers().keySet()) {
			Channel channel = null;
			if (userManager.isExistsChannel(key, SupportDevice.WEB)) {
				channel = userManager.getUser(key).getChannel(SupportDevice.WEB);
				channel.writeAndFlush(String.format("%s 님이 [%s]에서 접속하였습니다.\n", userKey, SupportDevice.WEB));
			}
			
			if (userManager.isExistsChannel(key, SupportDevice.APP)) {
				channel = userManager.getUser(key).getChannel(SupportDevice.APP);
				channel.writeAndFlush(String.format("%s 님이 [%s]에서 접속하였습니다.\n", userKey, SupportDevice.APP));
			}
		}
		
		if (!userManager.isExistsUser(userKey)) {
			ChatUser chatUser = new ChatUser(userKey, userKey, incoming.remoteAddress());
			if (!chatUser.isExistsChannel(SupportDevice.WEB)) {
				chatUser.addChannel(SupportDevice.WEB, incoming);
			}
			
			userManager.addUser(userKey, chatUser);
		}
		
		ChatUser chatUser = userManager.getUser(userKey);
		
		if (!chatRoom.isExistsUser(userKey)) {
			chatRoom.addMember(userKey, chatUser);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		String userKey = incoming.id().toString();
		log.debug("Channel Active of [SERVER] {}[{} / {}]", userKey, incoming.remoteAddress(), SupportDevice.WEB);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String message = (String) msg;
		Channel incoming = ctx.channel();
		
		String userKey = incoming.id().toString();
		
		log.debug("Channel Read of [SERVER] {}[{} / {}] > {}", userKey, incoming.remoteAddress(), SupportDevice.WEB, message);
		
		if (message == null || message.equals("")) {
			return;
		}
		
		RoomManager roomManager = RoomManager.getInstance();
		ChatRoom chatRoom = roomManager.getRoom("1");
		
		UserManager userManager = UserManager.getInstance();
		
		for (String key : chatRoom.getUsers().keySet()) {
			Channel channel = null;
			
			if (userManager.isExistsChannel(key, SupportDevice.WEB)) {
				channel = userManager.getUser(key).getChannel(SupportDevice.WEB);
				if (channel != incoming) {
					channel.writeAndFlush(String.format("%s > %s\r\n", userKey, message));
				}
			}
			
			if (userManager.isExistsChannel(key, SupportDevice.APP)) {
				channel = userManager.getUser(key).getChannel(SupportDevice.APP);
				if (channel != incoming) {
					channel.writeAndFlush(String.format("%s > %s\r\n", userKey, message));
				}
			}
		}
		
		if ("exit".equals(message.toLowerCase().trim())) {
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
		String userKey = incoming.id().toString();
		log.debug("Handler Removed of [SERVER] {}[{} / {}]", userKey, incoming.remoteAddress(), SupportDevice.WEB);
		
		RoomManager roomManager = RoomManager.getInstance();
		ChatRoom chatRoom = roomManager.getRoom("1");
		
		UserManager userManager = UserManager.getInstance();
		
		for (String key : chatRoom.getUsers().keySet()) {
			Channel channel = null;
			if (userManager.isExistsChannel(key, SupportDevice.WEB)) {
				channel = userManager.getUser(key).getChannel(SupportDevice.WEB);
				channel.writeAndFlush(String.format("%s 님이 접속을 종료하셨습니다.\n", userKey));
			}
			
			if (userManager.isExistsChannel(key, SupportDevice.APP)) {
				channel = userManager.getUser(key).getChannel(SupportDevice.APP);
				channel.writeAndFlush(String.format("%s 님이 [%s]에서 접속하였습니다.\n", userKey));
			}
		}
		
		chatRoom.removeMember(userKey);
		// TODO: Chat Room Delete
	}

}
