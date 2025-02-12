package com.umc.ttt.domain.book.dto;

import com.umc.ttt.domain.member.dto.MemberResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class BookResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchBookResultDTO {
        private List<BookInfoDTO> books;
        private Long nextCursor;
        private int limit;
        private boolean hasNext;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetBestSellersResultDTO {
        private List<BestSellerDTO> books;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BestSellerDTO {
        private Long id;
        private String cover;
        private String title;
        private String author;
        private String category;
        private String publisher;
        private String mainSentences;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuggestBooksByBookCategoryResultDTO {
        private List<BookInfoDTO> books;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuggestBooksForUserResultDTO {
        private String memberNickname;
        private List<BookInfoDTO> books;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuggestBooksByEditorResultDTO {
        private String bookLetterTitle;
        private List<BookInfoDTO> books;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookInfoDTO {
        private Long id;
        private String cover;
        private String title;
        private String author;
        private String category;
        private String publisher;
        private String itemLink;
        private boolean isScraped;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetBookDetailResultDTO {
        private Long id;
        private String cover;
        private String title;
        private String author;
        private String category;
        private String publisher;
        private int itemPage;
        private String description;
        private boolean hasEbook;
        private boolean isScraped;
        private String itemLink;
        private double userRating;
        private double totalRating;
        private List<ReviewDTO> reviews;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewDTO {
        private Long id;
        private String content;
        private double rating;
        private LocalDateTime createdAt;
        private MemberResponseDTO.MemberInfoDTO memberInfo;
    }
}