package com.umc.ttt.global.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneratedToken {
    private String accessToken;
    private String refreshToken;

}
