package com.umc.ttt.domain.bookClub.repository;

import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookClubRepository extends JpaRepository<BookClub, Long> {
    boolean existsByBookLetterBookId(Long bookLetterBookId);

    @Query("SELECT bc FROM BookClub bc " +
            "JOIN BookClubMember bcm ON bc.id = bcm.bookClub.id " +
            "WHERE bcm.member = :member " +
            "AND bc.endDate BETWEEN :now AND :endOfMonth")
    List<BookClub> findMyBookClubs(Member member, LocalDate now, LocalDate endOfMonth);

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
