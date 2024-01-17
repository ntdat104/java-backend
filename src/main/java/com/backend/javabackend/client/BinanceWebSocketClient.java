package com.backend.javabackend.client;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.backend.javabackend.handler.SocketHandler;

public class BinanceWebSocketClient extends WebSocketClient {

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
        // Process the received message

        SocketHandler.forwardMessageToAll(message);
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

}
