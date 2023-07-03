package com.uynite.chat.client;

import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.time.LocalDateTime;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ChatClient {

    private static final String WEBSOCKET_URL = "ws://localhost:8080/chat";

    public static void main(String[] args) {
        WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(
                new StandardWebSocketClient(),
                new ChatWebSocketHandler(),
                WEBSOCKET_URL
        );
        connectionManager.start();
    }

    static class ChatWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            String userId = "123"; // Replace with the actual user ID
            String receiverId = "456";
            String message = "Hello, chat server!"; // Replace with the actual message

            // Construct the chat message JSON payload
            String payload = "{\"senderId\":\"" + userId + "\","
            					+"\"receiverId\":\"" + receiverId + "\""
            					+"\"message\":\"" + message + "\""
            					+"\"timestamp\":\"" + LocalDateTime.now().toString() + "\""
            					+"}";

            // Send the chat message as a WebSocket text message
            session.sendMessage(new TextMessage(payload));
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            // Handle incoming WebSocket text messages
        }
    }
}

