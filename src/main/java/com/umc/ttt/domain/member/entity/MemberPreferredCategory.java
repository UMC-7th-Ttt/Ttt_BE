package com.umc.ttt.domain.member.entity;

import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.entity.BookFormatCategory;
import com.umc.ttt.domain.place.entity.enums.PlaceCategory;
import com.umc.ttt.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class MemberPreferredCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_prefered_category_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_category_id") // bookCategory는 선택이므로 nullable = true
    private BookCategory bookCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_format_id")
    private BookFormatCategory bookFormatCategory;
}
