package com.backend.javabackend.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketHandler implements WebSocketHandler {

    private static final List<WebSocketSession> sessions = new ArrayList<>();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final int PING_INTERVAL_SECONDS = 100; // Change this to your desired interval

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("Websocket closed");
        close();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Websocket established");
        sessions.add(session);
        startPingPong(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = message.getPayload().toString();
        log.info("msg recieve: {}", payload);

        if ("ping".equals(payload)) {
            sendMessage(session, "pong");
        }

        if ("time".equals(payload)) {
            sendMessage(session, new Date().toString());
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

    private void startPingPong(WebSocketSession session) {
        executorService.scheduleAtFixedRate(() -> {
            try {
                sendMessage(session, String.format("%s: Ping!", new Date().toInstant().toEpochMilli()));
                log.info("{}: Ping!", new Date().toInstant().toEpochMilli());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, PING_INTERVAL_SECONDS, TimeUnit.MILLISECONDS);
    }

    public void close() {
        executorService.shutdown();
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

}
