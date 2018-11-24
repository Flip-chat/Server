package com.flipchat.server.info;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.flipchat.server.env.SupportDevice;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

public class ChatUser {

	private ConcurrentMap<SupportDevice, ChannelInfo> channelInfoMap = new ConcurrentHashMap<>(SupportDevice.values().length);
	private String key;
	private String name;
	private String remoteAddress;
	
	public ChatUser() {}
	
	public ChatUser(String key, String name, SocketAddress socketAddress) {
		this.key = key;
		this.name = name;
		this.remoteAddress = socketAddress.toString();
	}
	
	public boolean isExistsChannel(SupportDevice supportDevice) {
		return channelInfoMap.containsKey(supportDevice);
	}
	
	public ConcurrentMap<SupportDevice, ChannelInfo> getChannelInfo() {
		return channelInfoMap;
	}
	
	public Channel getChannel(SupportDevice supportDevice) {
		if (!isExistsChannel(supportDevice)) {
			return null;
		}
		
		return channelInfoMap.get(supportDevice).getChannel();
	}
	
	public ChannelId getChannelId(SupportDevice supportDevice) {
		if (!isExistsChannel(supportDevice)) {
			return null;
		}
		
		return channelInfoMap.get(supportDevice).getChannelId();
	}
	
	public boolean addChannel(SupportDevice supportDevice, Channel channel) {
		if (!isExistsChannel(supportDevice)) {
			synchronized (channelInfoMap) {
				if (!isExistsChannel(supportDevice)) {
					ChannelInfo channelInfo = new ChannelInfo(channel);
					channelInfoMap.put(supportDevice, channelInfo);
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public ChatUser removeChannel(SupportDevice supportDevice) {
		channelInfoMap.remove(supportDevice);
		return this;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

}
