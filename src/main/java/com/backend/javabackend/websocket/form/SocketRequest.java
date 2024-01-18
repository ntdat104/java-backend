package com.backend.javabackend.websocket.form;

import java.util.List;

import com.backend.javabackend.websocket.enums.SocketMethod;
import com.backend.javabackend.websocket.enums.SocketType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocketRequest {

    private SocketMethod method;

    private SocketType type;

    private List<String> params;

}
