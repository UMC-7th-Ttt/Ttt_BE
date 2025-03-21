package com.umc.ttt.domain.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

public class ReviewRequestDTO {
    @Getter
    public static class AddUpdateDto{
        String title;
        String content;

        double bookRanking;
        double placeRanking;

        boolean isSecret;

        @NotNull(message = "날짜는 필수입니다.")
        LocalDate writeDate;

        Long bookId;
        Long placeId;
    }

    @Getter
    public static class AddUpdateBookReviewDto{
        Long bookId;
        double bookRanking;
    }

    @Getter
    public static class AddUpdatePlaceReviewDto{
        Long placeId;
        double placeRanking;
    }
}
