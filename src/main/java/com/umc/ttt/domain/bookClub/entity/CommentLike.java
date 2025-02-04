package com.umc.ttt.domain.bookClub.entity;

import com.umc.ttt.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class CommentLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_like_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;  // 좋아요 한 댓글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_club_member_id")
    private BookClubMember bookClubMember;  // 좋아요 한 멤버
}
