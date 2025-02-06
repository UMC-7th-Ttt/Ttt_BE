package com.umc.ttt.domain.bookLetter.handler;

import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.GeneralException;

public class BookLetterCategoryHandler extends GeneralException {
    public BookLetterCategoryHandler(ErrorStatus errorStatus) {
        super(errorStatus);
    }

}
