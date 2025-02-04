package com.umc.ttt.domain.bookClub.entity;

import com.umc.ttt.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class ReadingRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_record_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;  // 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 내용

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imgUrl;  // 인증 사진

    @Column(nullable = false)
    private int currentPage;    // 현재까지 읽은 쪽수

    @Column(nullable = false)
    private boolean isSecret;   // 공개 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_club_member_id")
    private BookClubMember bookClubMember;
}
