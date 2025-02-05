package com.umc.ttt.domain.member.repository;

import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.entity.BookFormatCategory;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.entity.MemberPreferredCategory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberPreferredCategoryRepository extends JpaRepository<MemberPreferredCategory, Long> {
    boolean existsMemberPreferredCategoriesByBookCategoryAndMember_Id(BookCategory bookCategory, Long member_id);
    boolean existsByBookFormatCategoryAndMember_Id(BookFormatCategory bookFormatCategory, Long member_id);

    List<MemberPreferredCategory> findByMember(Member member);

    @Query("SELECT mpc.bookCategory FROM MemberPreferredCategory mpc WHERE mpc.member.id = :memberId AND mpc.bookCategory IS NOT NULL")
    List<BookCategory> findBookCategoriesByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT mpc.bookFormatCategory FROM MemberPreferredCategory mpc WHERE mpc.member.id = :memberId AND mpc.bookFormatCategory IS NOT NULL")
    List<BookFormatCategory> findBookFormatsByMemberId(@Param("memberId") Long memberId);
}
