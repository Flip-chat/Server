package com.wonchiyoung.filp.server.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wonchiyoung.filp.server.env.SupportDevice;
import com.wonchiyoung.filp.server.info.ChatUser;

public class UserManager {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private static UserManager userManager = new UserManager();
	
	private ConcurrentMap<String, ChatUser> users = new ConcurrentHashMap<>(50);
	
	private UserManager() {
		System.out.println("User manager created.");
	}
	
	public static UserManager getInstance() {
		return userManager;
	}
	
	public void initialize() {
		// TODO: DB read
	}
	
	public boolean isExistsUser(String key) {
		return users.containsKey(key);
	}
	
	public boolean isExistsChannel(String key, SupportDevice supportDevice) {
		return isExistsUser(key) && users.get(key).isExistsChannel(supportDevice);
	}
	
	public ChatUser getUser(String key) {
		if (!users.containsKey(key)) {
			throw new IllegalStateException(String.format("Chat user not found. Invalid user key=[%s]", key));
		}
		return users.get(key);
	}
	
	public ConcurrentMap<String, ChatUser> getUsers() {
		return users;
	}
	
	public boolean addUser(String key, ChatUser chatUser) {
		if (users.putIfAbsent(key, chatUser) != null) {
			log.warn("[addUser]!!!!![Exists Key]!!!!! [UserKey={}] [ChatUser={}]", key, chatUser);
			return false;
		}
		
		log.debug("[addUser] [UserKey={}] [ChatUser={}]", key, chatUser);
		return true;
	}
	
	public UserManager removeUser(String key) {
		ChatUser chatUser = users.remove(key);
		log.debug("[RemoveUser] [UserKey={}] [ChatUser={}]", key, chatUser);
		return this;
	}

}
