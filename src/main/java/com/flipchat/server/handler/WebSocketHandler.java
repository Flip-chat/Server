package com.flipchat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if (msg instanceof WebSocketFrame) {
			
			log.debug("This is a WebSocket frame");
			log.debug("Client Channel : {}", ctx.channel());
			if (msg instanceof BinaryWebSocketFrame) {
				log.debug("BinaryWebSocketFrame Received : ");
				log.debug("{}", ((BinaryWebSocketFrame) msg).content());
				ctx.writeAndFlush(msg);
			} else if (msg instanceof TextWebSocketFrame) {
				log.debug("TextWebSocketFrame Received : ");
				log.debug("{}", ((TextWebSocketFrame) msg).content());
				ctx.writeAndFlush(msg);
			} else if (msg instanceof PingWebSocketFrame) {
				log.debug("PingWebSocketFrame Received : ");
				log.debug("{}", ((PingWebSocketFrame) msg).content());
				ctx.writeAndFlush(msg);
			} else if (msg instanceof PongWebSocketFrame) {
				log.debug("PongWebSocketFrame Received : ");
				log.debug("{}", ((PongWebSocketFrame) msg).content());
				ctx.writeAndFlush(msg);
			} else if (msg instanceof CloseWebSocketFrame) {
				log.debug("CloseWebSocketFrame Received : ");
				log.debug("ReasonText : {}", ((CloseWebSocketFrame) msg).reasonText());
				log.debug("StatusCode : {}", ((CloseWebSocketFrame) msg).statusCode());
				ctx.writeAndFlush(msg);
			} else {
				log.debug("Unsupported WebSocketFrame");
			}
		}
	}

	
}
