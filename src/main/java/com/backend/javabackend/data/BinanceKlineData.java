package com.backend.javabackend.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BinanceKlineData {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("result")
    private Object result;

    @JsonProperty("stream")
    private String stream;

    @JsonProperty("data")
    private KlineData data;

    @Data
    public static class KlineData {

        @JsonProperty("e")
        private String event;

        @JsonProperty("E")
        private long eventTime;

        @JsonProperty("s")
        private String symbol;

        @JsonProperty("k")
        private Kline kline;

        @Data
        public static class Kline {

            @JsonProperty("t")
            private long startTime;

            @JsonProperty("T")
            private long endTime;

            @JsonProperty("s")
            private String symbol;

            @JsonProperty("i")
            private String interval;

            @JsonProperty("f")
            private long firstTradeId;

            @JsonProperty("L")
            private long lastTradeId;

            @JsonProperty("o")
            private String openPrice;

            @JsonProperty("c")
            private String closePrice;

            @JsonProperty("h")
            private String highPrice;

            @JsonProperty("l")
            private String lowPrice;

            @JsonProperty("v")
            private String volume;

            @JsonProperty("n")
            private int numberOfTrades;

            @JsonProperty("x")
            private boolean isFinal;

            @JsonProperty("q")
            private String quoteAssetVolume;

            @JsonProperty("V")
            private String takerBuyBaseAssetVolume;

            @JsonProperty("Q")
            private String takerBuyQuoteAssetVolume;

            @JsonProperty("B")
            private String ignore;
        }
    }

}
