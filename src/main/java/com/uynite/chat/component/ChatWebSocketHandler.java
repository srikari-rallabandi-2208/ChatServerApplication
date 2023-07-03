package com.uynite.chat.component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uynite.chat.model.ChatMessage;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

	private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        
        // Parse the message payload using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);

        String senderId = chatMessage.getSenderId();
        String receiverId = chatMessage.getReceiverId();
        String messageContent = chatMessage.getMessage();

        chatMessage.setTimestamp(LocalDateTime.now().toString());

        // Process the chat message and send it to the receiver's WebSocket session
        WebSocketSession receiverSession = sessions.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(payload));
        }

        // Save the chat message to the database or any other necessary processing
        // You can store the chat history in the database or any other persistent storage
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        System.out.println("After Connection Established");
        // Add the session to the sessions map
        sessions.put(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);
        System.out.println("After Connection Closed");
        // Remove the session from the sessions map
        sessions.remove(userId);
    }

    private String getUserIdFromSession(WebSocketSession session) {
        // Extract the userId from the WebSocket session attributes or message headers
        // This can be based on your authentication mechanism or any other logic
        // For simplicity, let's assume the userId is passed as a query parameter in the connection URL
        Map<String, Object> attributes = session.getAttributes();
        String userId = (String) attributes.get("userId");
        System.out.println("Got Userid From Session"+userId);
        return userId;
    }
}
