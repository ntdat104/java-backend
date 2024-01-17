package com.backend.javabackend.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class BinanceWebSocketClient extends WebSocketClient {

    private WebSocketSession session;

    private static final URI uri = URI.create("wss://stream.binance.com/stream");

    public BinanceWebSocketClient() {
        super(uri);
        this.connect();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("BinanceWebSocket connection opened");
        // schedulePing(20000);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("BinanceWebSocket Received message: " + message);
        if (session != null) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("BinanceWebSocket connection closed, code: " + code + ", reason: " + reason);
        scheduleReconnect(3000);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("BinanceWebSocket error: " + ex.getMessage());
        // Handle the error, you may also consider reconnecting here
    }

    private void scheduleReconnect(long delay) {
        // Schedule a task to reconnect after the specified delay
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                reconnect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void handleMessage(WebSocketSession session, String message) {
        this.session = session;
        this.send(message);
    }

}
