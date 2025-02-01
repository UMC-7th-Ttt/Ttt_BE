package com.umc.ttt.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDate;
import java.util.List;

public class ReviewResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddUpdateResultDTO{
        Long reviewId;
    }

    // 캘린더로 서평 목록 보기
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class reviewCalendarListDTO{
        List<reviewCalendarDTO> reviewList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class reviewCalendarDTO{
        private Long id;
        private String cover;
        private LocalDate writeDate;
    }

}
