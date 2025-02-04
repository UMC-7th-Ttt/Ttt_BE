package com.umc.ttt.domain.bookLetter.entity;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class BookLetter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_letter_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;   // 제목

    @Column(nullable = false)
    private String subtitle;    // 부제목

    @Column(nullable = false)
    private String editor;  // 에디터

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 내용

    @Column(nullable = false, columnDefinition = "TEXT")
    private String coverImg;    // 대표 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "bookLetter", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookLetterBook> books = new ArrayList<>();

}
