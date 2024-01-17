package com.backend.javabackend.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class BinanceWebSocketClient extends WebSocketClient {

    public BinanceWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("WebSocket connection opened");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        // Process the received message
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket connection closed, code: " + code + ", reason: " + reason);
        // Reconnect after a delay (e.g., 5 seconds)
        scheduleReconnect(5000);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
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

    public static void main(String[] args) throws URISyntaxException {
        BinanceWebSocketClient client = new BinanceWebSocketClient(URI.create("wss://stream.binance.com/stream"));
        client.connect();
    }

}
