package com.sportlink.sportlink.socket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    // Map to store WebSocket sessions by locationId
    private final ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extract locationId from query parameters
        String locationIdStr = session.getUri().getQuery().split("=")[1];
        Long locationId = Long.parseLong(locationIdStr);
        sessions.put(locationId, session);
        System.out.println("Connection established for location: " + locationId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove session from the map
        sessions.values().remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    public void sendCodeToLocation(Long locationId, String code) {
        WebSocketSession session = sessions.get(locationId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(code));
                System.out.println("Code sent to location " + locationId + ": " + code);
            } catch (Exception e) {
                System.err.println("Error sending code: " + e.getMessage());
            }
        } else {
            System.err.println("No active connection for location: " + locationId);
        }
    }
}
