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
        private List<reviewCalendarDTO> reviewList;
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

    // 서평 모아보기
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class reviewListDTO{
        private List<reviewDTO> reviewList;
        private Long nextCursor;
        private int limit;
        private boolean hasNext;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class reviewDTO{
        private Long id;
        private String cover;
    }

    // 서평 상세 보기
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class bookDTO{
        private Long id;
        private String title;
        private String author;
        private String cover;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class placeDTO{
        private Long id;
        private String title;
        private String address;
        private String image;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class reviewInfoDTO{
        private Long id;
        private String title;
        private String content;
        private boolean isSecret;
        private double bookRanking;     // bookRanking이 0이라면 리뷰에 책은 등록하지 않은 것입니다.
        private double placeRanking;    // placeRanking이 0이라면 리뷰에 장소는 등록하지 않은 것입니다.
        private LocalDate writeDate;
        private bookDTO book;
        private placeDTO place;
    }

}
