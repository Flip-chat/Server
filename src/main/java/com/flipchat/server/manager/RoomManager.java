package com.flipchat.server.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipchat.server.info.ChatRoom;

public class RoomManager {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private static RoomManager roomManager = new RoomManager();
	
	private ConcurrentMap<String, ChatRoom> rooms = new ConcurrentHashMap<>(20);
	
	private RoomManager() {
		System.out.println("Room manager created.");
	}
	
	public static RoomManager getInstance() {
		return roomManager;
	}
	
	public void initialize() {
		// TODO: DB read
	}
	
	public boolean isExistsRoom(String id) {
		return rooms.containsKey(id);
	}
	
	public boolean isExistsUser(String id, String userKey) {
		return isExistsRoom(id) && rooms.get(id).isExistsUser(userKey);
	}
	
	public ChatRoom getRoom(String id) {
		if (!rooms.containsKey(id)) {
			throw new IllegalStateException(String.format("Chat room not found. Invalid room id=[%s]", id));
		}
		return rooms.get(id);
	}
	
	public ConcurrentMap<String, ChatRoom> getRooms() {
		return rooms;
	}
	
	public boolean addRoom(String id, ChatRoom chatRoom) {
		if (rooms.putIfAbsent(id, chatRoom) != null) {
			log.warn("[addRoom]!!!!![Exists Id]!!!!! [RoomId={}] [ChatRoom={}]", id, chatRoom);
			return false;
		}
		
		log.debug("[addRoom] [RoomId={}] [ChatRoom={}]", id, chatRoom);
		return true;
	}
	
	public RoomManager removeRoom(String id) {
		ChatRoom chatRoom = rooms.remove(id);
		log.debug("[removeRoom] [RoomId={}] [ChatRoom={}]", id, chatRoom);
		return this;
	}

}
