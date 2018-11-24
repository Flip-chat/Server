package com.flipchat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	private WebSocketServerHandshaker handshaker;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if (msg instanceof HttpRequest) {
			HttpRequest httpRequest = (HttpRequest) msg;
			
			log.trace("Http Request Received");
			
			HttpHeaders headers = httpRequest.headers();
			log.trace("Connection : {}", headers.get("Connection"));
			log.trace("Upgrade : {}", headers.get("Upgrade"));
			
			if ("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION))
					&& "WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))) {
				
				ctx.pipeline().replace(this, "websocketHandler", new WebSocketHandler());
				
				log.debug("WebSocketHandler added to the pipeline");
				log.debug("Opened Channel : {}", ctx.channel().id());
				log.debug("Handshaking....");
				
				handleHandshake(ctx, httpRequest);
				log.debug("Handshake is done");
			}
		} else {
			log.trace("Incoming request is unknown");
		}
	}
	
	protected void handleHandshake(ChannelHandlerContext ctx, HttpRequest request) {
		WebSocketServerHandshakerFactory webSocketServerHandshakerFactory
				= new WebSocketServerHandshakerFactory(getWebSocketURL(request), null, true);
		handshaker = webSocketServerHandshakerFactory.newHandshaker(request);
		
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), request);
		}
	}
	
	protected String getWebSocketURL(HttpRequest request) {
		log.debug("Request URL : {}", request.uri());
		String url = "ws://" + request.headers().get("Host") + request.uri();
		log.debug("Constructecd URL : {}", url);
		return url;
	}
	
	

}
