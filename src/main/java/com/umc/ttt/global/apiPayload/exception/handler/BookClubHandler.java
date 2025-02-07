package com.umc.ttt.global.apiPayload.exception.handler;

import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.GeneralException;

public class BookClubHandler extends GeneralException {
    public BookClubHandler(ErrorStatus errorStatus) {
      super(errorStatus);
    }
}
