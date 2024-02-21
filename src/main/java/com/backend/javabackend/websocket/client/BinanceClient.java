package com.backend.javabackend.websocket.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.backend.javabackend.websocket.data.BinanceKlineData;
import com.backend.javabackend.websocket.enums.SocketMethod;
import com.backend.javabackend.websocket.form.BinanceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinanceClient extends WebSocketClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private HashSet<String> params = new HashSet<>();

    private Map<WebSocketSession, HashSet<String>> sessions = new LinkedHashMap<>();

    public BinanceClient() {
        super(URI.create("wss://stream.binance.com/stream"));
        this.connect();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("Binance socket is opened");

        if (params.size() > 0) {
            try {
                var request = new BinanceRequest();
                request.setId(1);
                request.setMethod(SocketMethod.SUBSCRIBE);
                request.setParams(new ArrayList<>(params));
                send(objectMapper.writeValueAsString(request));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            var data = objectMapper.readValue(message, BinanceKlineData.class);

            for (Map.Entry<WebSocketSession, HashSet<String>> entry : sessions.entrySet()) {
                WebSocketSession session = entry.getKey();
                HashSet<String> sessionParams = entry.getValue();

                if (session.isOpen() && sessionParams.contains(data.getStream())) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Binance is closed, code: " + code + ", reason: " + reason);
        scheduleReconnect(3000);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Binance has error: " + ex.getMessage());
    }

    private void scheduleReconnect(long delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                reconnect();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }).start();
    }

    public void sendMessage(WebSocketSession session, BinanceRequest request) {
        try {
            if (this.sessions.containsKey(session) && session.isOpen()) {
                HashSet<String> params = this.sessions.get(session);

                if (request.getMethod().equals(SocketMethod.SUBSCRIBE)) {
                    params.addAll(request.getParams());
                } else {
                    params.removeAll(request.getParams());
                }

                this.sessions.put(session, params);
            } else {
                this.sessions.put(session, new HashSet<>(request.getParams()));
            }

            if (request.getMethod().equals(SocketMethod.SUBSCRIBE)) {
                params.addAll(request.getParams());
            }

            this.send(objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
