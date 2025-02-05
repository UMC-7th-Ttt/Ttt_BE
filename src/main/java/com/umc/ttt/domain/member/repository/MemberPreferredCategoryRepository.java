package com.umc.ttt.domain.member.repository;

import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.entity.BookFormatCategory;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.entity.MemberPreferredCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberPreferredCategoryRepository extends JpaRepository<MemberPreferredCategory, Long> {
    boolean existsByBookCategoryAndMember(BookCategory bookCategory, Member member);
    boolean existsByBookFormatCategoryAndMember(BookFormatCategory bookFormatCategory, Member member);

    List<MemberPreferredCategory> findByMember(Member member);
}
