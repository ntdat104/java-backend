package com.backend.javabackend.handler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketHandler implements WebSocketHandler {

    private static final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("Websocket closed");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Websocket established");
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = message.getPayload().toString();
        log.info("msg recieve: {}", payload);

        try {
            var binance = new WebSocketClient(URI.create("wss://stream.binance.com/stream")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to Binance WebSocket");
                    // Send the subscription message after connecting
                    send("{\"method\":\"SUBSCRIBE\",\"params\":[\"btcusdt@kline_1s\"],\"id\":1}");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Received message from Binance WebSocket: " + message);
                    try {
                        sendMessage(session, message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Handle the received message as needed
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Connection to Binance WebSocket closed. Code: " + code + ", Reason: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("Error in Binance WebSocket connection: " + ex.getMessage());
                }
            };

            binance.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Error {}", exception);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void sendMessage(WebSocketSession session, String message) throws IOException {
        session.sendMessage(new TextMessage(message));
    }

    public void sendMessageToAll(String message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void forwardMessageToAll(String message) {
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
