package com.umc.ttt.domain.book.dto;

import lombok.*;

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
    public static class SuggestBooksResultDTO {
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
    }
}