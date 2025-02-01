package com.umc.ttt.domain.member.handler;

import com.umc.ttt.global.apiPayload.code.BaseErrorCode;
import com.umc.ttt.global.apiPayload.exception.GeneralException;

public class MemberHandler extends GeneralException {
    public MemberHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
