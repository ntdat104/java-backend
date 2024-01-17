package com.backend.javabackend.form;

import java.util.List;

import com.backend.javabackend.enums.BinanceMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BinanceRequest {
    private BinanceMethod method;

    private List<String> params;

    private Integer id;
}
