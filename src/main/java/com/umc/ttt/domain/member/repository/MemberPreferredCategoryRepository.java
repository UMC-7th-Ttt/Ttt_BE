package com.umc.ttt.domain.member.repository;

import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.entity.BookFormatCategory;
import com.umc.ttt.domain.member.entity.MemberPreferredCategory;
import com.umc.ttt.domain.place.entity.enums.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberPreferredCategoryRepository extends JpaRepository<MemberPreferredCategory, Long> {
    boolean existsMemberPreferredCategoriesByBookCategoryAndMember_Id(BookCategory bookCategory, Long member_id);
    boolean existsByBookFormatCategoryAndMember_Id(BookFormatCategory bookFormatCategory, Long member_id);
}
