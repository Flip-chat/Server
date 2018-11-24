package com.flipchat.server.info;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

public class ChannelInfo {

	private Channel channel;
	private ChannelId channelId;
	
	public ChannelInfo() {}
	
	public ChannelInfo(Channel channel) {
		this.channel = channel;
		this.channelId = channel.id();
	}
	
	
	public void setChannel(Channel channel) {
		this.channel = channel;
		this.channelId = channel.id();
	}
	public Channel getChannel() {
		return channel;
	}
	public ChannelId getChannelId() {
		return channelId;
	}

}
