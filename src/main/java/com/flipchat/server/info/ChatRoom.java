package com.flipchat.server.info;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChatRoom {

	private String id;
	private ConcurrentMap<String, ChatUser> users = new ConcurrentHashMap<>(2);
	
	public ChatRoom() {}
	
	public ChatRoom(String id) {
		this.id = id;
	}
	
	
	public boolean isExistsUser(String key) {
		return users.containsKey(key);
	}
	
	public ConcurrentMap<String, ChatUser> getUsers() {
		return users;
	}
	
	public ChatUser getUser(String key) {
		if (!isExistsUser(key)) {
			return null;
		}
		
		return users.get(key);
	}
	
	public ChatRoom addMember(String key, ChatUser chatUser) {
		users.put(key, chatUser);
		return this;
	}
	
	public ChatRoom removeMember(String key) {
		users.remove(key);
		return this;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
