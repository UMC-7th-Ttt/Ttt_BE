package com.umc.ttt.domain.review.dto;

import com.umc.ttt.domain.review.validator.annotation.RankingRange;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

public class ReviewRequestDTO {
    @Getter
    public static class AddUpdateDto{
        String title;
        String content;

        @RankingRange
        double bookRanking;
        double placeRanking;

        boolean isSecret;

        @NotNull
        LocalDate writeDate;

        @NotNull
        Long memberId;

        Long bookId;
        Long placeId;
    }
}
