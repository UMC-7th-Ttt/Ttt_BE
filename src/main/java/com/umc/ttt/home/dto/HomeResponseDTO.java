package com.umc.ttt.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class HomeResponseDTO {
//    메인 베너
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class mainBannerDTO{
        Long bookLetterId;
        String title;
        String subTitle;
        String editor;
        String coverImg;
    }

//    유저 활동 (북클럽)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class bookClubDTO{
        Long bookClubId;
        Long bookId;
        String bookTitle;
        String bookCover;
        Integer completionRate;  // 완독률
    }

//    맞춤 북레터
    // 북레터에 담긴 책
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class bookLetterBookDTO{
        String bookCoverImg;
    }

    // 북레터
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class recommendBookLetterDTO {
        Long bookLetterId;
        String bookLetterTitle;
        List<bookLetterBookDTO> bookList;
    }

//    리마인드 (독서평)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class remindReviewDTO {
        Long bookId;
        String bookTitle;
        String bookCover;
        LocalDate writeDate;
        String content; // 최대 30자
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class viewHomeResultDTO{
        String nickname;
        List<mainBannerDTO> mainBannerList;
        List<bookClubDTO> bookClubList;
        List<recommendBookLetterDTO> bookLetterList;
        List<remindReviewDTO> remindReviewList;

    }
}
