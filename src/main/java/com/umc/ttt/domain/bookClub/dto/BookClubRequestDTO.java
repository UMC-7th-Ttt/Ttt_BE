package com.umc.ttt.domain.bookClub.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

public class BookClubRequestDTO {
    @Getter
    public static class AddUpdateDTO {

        @NotNull(message = "북레터 북의 아이디는 필수입니다.")
        Long bookLetterBookId;

        @NotNull(message = "활동 시작 날짜는 필수입니다.")
        LocalDate startDate;

        @NotNull(message = "활동 종료 날짜는 필수입니다.")
        LocalDate endDate;

        @NotNull(message = "추천 멘트는 필수입니다.")
        String comment;
    }
}
