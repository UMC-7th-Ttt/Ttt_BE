package com.umc.ttt.domain.bookLetter.bookLetterRepository;

import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookLetterRepository extends JpaRepository<BookLetter, Long> {
    List<BookLetter> findTop3ByOrderByCreatedAtDesc();

    @Query("SELECT bl FROM BookLetter bl " +
            "WHERE bl.bookCategory IN (SELECT mpc.bookCategory FROM MemberPreferredCategory mpc WHERE mpc.member.id = :memberId) " +
            "ORDER BY FUNCTION('RAND')")
    List<BookLetter> findRandomBookLettersByPreferredCategory(@Param("memberId") Long memberId, Pageable pageable);
}
