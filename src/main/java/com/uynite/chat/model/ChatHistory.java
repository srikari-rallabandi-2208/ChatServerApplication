package com.uynite.chat.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ChatHistory {
    
    @Id
    private String id;
    private String user1Id;
    private String user2Id;
    
    
    private List<ChatMessage> messages;
	public String getUser1Id() {
		return user1Id;
	}
	public void setUser1Id(String user1Id) {
		this.user1Id = user1Id;
	}
	public String getUser2Id() {
		return user2Id;
	}
	public void setUser2Id(String user2Id) {
		this.user2Id = user2Id;
	}
	public List<ChatMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<ChatMessage> messages) {
		this.messages = messages;
	}
    
    // Constructors, getters, setters
}