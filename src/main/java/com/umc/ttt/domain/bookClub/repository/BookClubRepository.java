package com.umc.ttt.domain.bookClub.repository;

import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookLetter.entity.BookLetterBook;
import com.umc.ttt.domain.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookClubRepository extends JpaRepository<BookClub, Long> {
    boolean existsByBookLetterBookId(Long bookLetterBookId);

    @Query("""
        SELECT bc FROM BookClub bc
        JOIN bc.bookLetterBook blb
        JOIN blb.book b
        WHERE YEAR(bc.startDate) = :year
        AND MONTH(bc.startDate) = :month
        AND (:cursor IS NULL OR b.title > :cursor)
        ORDER BY b.title ASC
    """)
    Slice<BookClub> findBookClubsWithCursor(@Param("year") int year,
                                            @Param("month") int month,
                                            @Param("cursor") String cursor,
                                            Pageable pageable);
}
