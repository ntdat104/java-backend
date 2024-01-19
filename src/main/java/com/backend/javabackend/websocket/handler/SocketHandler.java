package com.backend.javabackend.websocket.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.backend.javabackend.websocket.client.BinanceClient;
import com.backend.javabackend.websocket.enums.SocketMethod;
import com.backend.javabackend.websocket.enums.SocketType;
import com.backend.javabackend.websocket.form.BinanceRequest;
import com.backend.javabackend.websocket.form.SocketRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BinanceClient binanceClient = new BinanceClient();

    private static final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("Websocket closed");
        binanceClient.close();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Websocket established {}", session.getId());
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            var request = objectMapper.readValue(message.getPayload().toString(), SocketRequest.class);

            if (request.getType().equals(SocketType.CRYPTO)) {
                var binanceRequest = new BinanceRequest();
                binanceRequest.setId(1);
                binanceRequest.setMethod(SocketMethod.SUBSCRIBE);
                binanceRequest.setParams(request.getParams());
                binanceClient.sendMessage(session, binanceRequest);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Error {}", exception);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendMessageToAll(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

}
