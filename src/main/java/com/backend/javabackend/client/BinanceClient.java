package com.backend.javabackend.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import com.backend.javabackend.data.BinanceKlineData;
import com.backend.javabackend.enums.BinanceMethod;
import com.backend.javabackend.form.BinanceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class BinanceClient {

    private WebSocketClient webSocketClient;

    private final List<String> params = new ArrayList<String>(
            Arrays.asList("btcusdt@kline_1s", "bnbusdt@kline_1s", "adausdt@kline_1s"));

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void connect() {
        try {
            String binanceWebSocketUrl = "wss://stream.binance.com/stream";
            webSocketClient = new WebSocketClient(URI.create(binanceWebSocketUrl)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to Binance WebSocket");
                    // Send the subscription message after connecting
                    var request = new BinanceRequest(BinanceMethod.SUBSCRIBE, params, 1);
                    try {
                        send(objectMapper.writeValueAsString(request));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onMessage(String message) {
                    try {
                        BinanceKlineData data = objectMapper.readValue(message, BinanceKlineData.class);

                        if (data.getData() != null) {
                            String symbol = data.getData().getKline().getSymbol();
                            Long time = data.getData().getKline().getEndTime();
                            Double c = Double.valueOf(data.getData().getKline().getClosePrice());
                            log.info("time: {}, {}: {}", time, symbol, c);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            webSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

}
