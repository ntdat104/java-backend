package com.backend.javabackend.websocket.form;

import java.util.List;

import com.backend.javabackend.websocket.enums.SocketMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BinanceRequest {
    private SocketMethod method;

    private List<String> params;

    private Integer id;
}
