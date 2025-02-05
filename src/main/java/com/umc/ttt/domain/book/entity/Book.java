package com.umc.ttt.domain.book.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;   // 상품명

    @Column(nullable = false)
    private String author;  // 저자

    @Column(nullable = false)
    private String isbn;    // isbn

    @Column(nullable = false)
    private String cover;   // 책 표지

    @Column(nullable = false)
    private String publisher;   // 출판사

    @Column(nullable = false)
    private int bestRank;  // 베스트셀러 순위

    @Column(nullable = false)
    private int itemPage;    // 분량

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // 책 소개(상품 설명)

    @Column(nullable = false)
    private boolean hasEbook;    // E북 등록 여부

    @Column(nullable = false)
    private double rating;  // 평점

    @Column(nullable = false)
    private String link;    // 상품 링크

    // 프리미엄 데이터
    private String toc; // 목차

    private String publisherDescription;    // 출판사 제공 책소개

    private String mainSentences;    // 주요 문장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_category_id")
    private BookCategory bookCategory;

    public void updateRating(double averageBookRating) {
        this.rating = averageBookRating;
    }
}
