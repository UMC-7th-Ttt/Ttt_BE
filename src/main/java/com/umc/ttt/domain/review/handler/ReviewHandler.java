package com.umc.ttt.domain.review.handler;

import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.GeneralException;

public class ReviewHandler extends GeneralException {
    public ReviewHandler(ErrorStatus errorStatus) {super(errorStatus);}
}
