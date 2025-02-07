package com.umc.ttt.global.apiPayload.exception.handler;

import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.GeneralException;

public class BookLetterBookHandler extends GeneralException {
    public BookLetterBookHandler(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
