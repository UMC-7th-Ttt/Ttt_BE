package com.umc.ttt.domain.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umc.ttt.domain.book.entity.BookCategory;
import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookFetchDTO {
    private String version;
    private String logo;
    private String title;
    private String link;
    private String pubDate;
    private int totalResults;
    private int startIndex;
    private int itemsPerPage;
    private String query;
    private int searchCategoryId;
    private String searchCategoryName;
    private List<Item> item;

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String title;
        private String author;
        private String cover;
        private String isbn;
        private String isbn13;
        private String publisher;
        private String description;
        private int bestRank;
        private String link;
        private int itemPage;
        private boolean hasEbook;
        private BookCategory category;

        @JsonProperty("subInfo")
        private void unpackSubInfo(SubInfo subInfo) {
            this.hasEbook = subInfo != null && subInfo.getEbookList() != null && !subInfo.getEbookList().isEmpty();
            this.itemPage = subInfo.getItemPage();
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SubInfo {
            private int itemPage;
            private List<?> ebookList;
        }
    }
}