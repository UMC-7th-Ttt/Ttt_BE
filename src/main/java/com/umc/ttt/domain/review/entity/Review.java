package com.umc.ttt.domain.review.entity;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @Column(nullable = true)
    private String title;   // 서평 제목

    @Lob
    @Column(nullable = true)
    private String content; // 서평 내용

    @Column(nullable = true)
    private double bookRanking; // 책 별점

    @Column(nullable = true)
    private double placeRanking;    // 장소 별점

    @Column(nullable = false)
    private LocalDate writeDate;    // 작성 날짜

    @Column(nullable = false)
    private boolean isSecret;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    public void setInfo(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void setIsSecret(boolean isSecret) {
        this.isSecret = isSecret;
    }

    public void setBook(Book book, double bookRanking) {
        this.book = book;
        this.bookRanking = bookRanking;
    }

    public void setPlace(Place place, double placeRanking) {
        this.place = place;
        this.placeRanking = placeRanking;
    }
}
