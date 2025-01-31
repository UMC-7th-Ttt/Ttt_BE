package com.umc.ttt.domain.mail.handler;

import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.GeneralException;

public class EMailHandler extends GeneralException {
    public EMailHandler(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
