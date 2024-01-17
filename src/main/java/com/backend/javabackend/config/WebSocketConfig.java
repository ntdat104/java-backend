package com.backend.javabackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.backend.javabackend.client.BinanceClient;
import com.backend.javabackend.handler.SocketHandler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final BinanceClient binanceWebSocketClient;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(), "/ws").setAllowedOrigins("*");
        binanceWebSocketClient.connect();
    }

}
