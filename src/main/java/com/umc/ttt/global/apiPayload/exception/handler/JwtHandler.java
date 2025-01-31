package com.umc.ttt.global.apiPayload.exception.handler;

import com.umc.ttt.global.apiPayload.code.BaseErrorCode;
import com.umc.ttt.global.apiPayload.exception.GeneralException;

public class JwtHandler extends GeneralException {
    public JwtHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
