package com.umc.ttt.domain.bookClub.entity;

import com.umc.ttt.domain.bookClub.dto.BookClubRequestDTO;
import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import com.umc.ttt.domain.bookLetter.entity.BookLetterBook;
import com.umc.ttt.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class BookClub extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_club_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;    // 활동 시작 날짜

    @Column(nullable = false)
    private LocalDate endDate;    // 활동 종료 날짜

    @Column(nullable = false)
    private String comment; // 한줄 추천 멘트

    @Column(nullable = false)
    private int participantCount;   // 참여 현황

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="book_letter_book_id", nullable = true)
    private BookLetterBook bookLetterBook;

    public void setBookClub(BookClubRequestDTO.AddUpdateDTO request){
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.comment = request.getComment();
    }

    public void setBookLetterBook(BookLetterBook bookLetterBook){
        this.bookLetterBook = bookLetterBook;
    }

    public void setParticipantCount(int participantCount){
        this.participantCount = participantCount;
    }
}
