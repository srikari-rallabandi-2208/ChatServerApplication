package com.uynite.chat.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.uynite.chat.model.ChatMessage;
import org.bson.json.JsonObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

	private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /*
        adding the variables for the WebSocketHandler
     */
    private String senderId;
    private String receiverId;

    public static Map<String, WebSocketSession> getSessions() {
        return sessions;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println("In handleMessage ");
        String payload = (String) message.getPayload();
        System.out.println(payload);
        ChatMessage chatMessage = parsePayload(payload);
        System.out.println("SenderId:\t"+chatMessage.getSenderId());
        System.out.println("ReceiverId:\t"+chatMessage.getReceiverId());
        sessions.putIfAbsent(chatMessage.getSenderId(),session);
        sessions.putIfAbsent(chatMessage.getReceiverId(),session);

        // Process the chat message and send it to the receiver's WebSocket session
        WebSocketSession receiverSession = sessions.get(chatMessage.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(payload));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("In handleTextMessage \n\n Received message"+payload);
        ChatMessage chatMessage = parsePayload(payload);
        if (!sessions.containsKey(chatMessage.getSenderId()))
            sessions.put(chatMessage.getSenderId(),session);
        if (!sessions.containsKey(chatMessage.getReceiverId()))
            sessions.put(chatMessage.getReceiverId(),session);
        // Process the chat message and send it to the receiver's WebSocket session
        WebSocketSession receiverSession = sessions.get(chatMessage.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(payload));
        }

        // Save the chat message to the database or any other necessary processing
        // You can store the chat history in the database or any other persistent storage
    }

    protected ChatMessage parsePayload(String payload)  {
        // Parse the message payload using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = new ChatMessage();
        try {
            objectMapper.readValue(payload, ChatMessage.class);
        }catch (JsonProcessingException jpe){
            System.out.println(jpe.getMessage());
        }

        JsonObject jsonObject = new JsonObject(payload);
        chatMessage = new Gson().fromJson(payload, ChatMessage.class);
        System.out.println("Receiver Id post Gson:\n\n"+chatMessage.getReceiverId());
        return chatMessage;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //String userId = getUserIdFromSession(session);
        System.out.println("After Connection Established");
        // Add the session to the sessions map
        //sessions.put(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //String userId = getUserIdFromSession(session);
        System.out.println("After Connection Closed");
        // Remove the session from the sessions map
        sessions.remove(senderId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // Handle transport error
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    private String getUserIdFromSession(WebSocketSession session) {
        // Extract the userId from the WebSocket session attributes or message headers
        // This can be based on your authentication mechanism or any other logic
        // For simplicity, let's assume the userId is passed as a query parameter in the connection URL

        System.out.println("Got Userid From Session"+sessions.get(senderId));
        return senderId;
    }
}
