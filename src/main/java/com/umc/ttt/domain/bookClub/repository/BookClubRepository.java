package com.umc.ttt.domain.bookClub.repository;

import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookClubRepository extends JpaRepository<BookClub, Long> {
    boolean existsByBookLetterBookId(Long bookLetterBookId);

    @Query("SELECT bc FROM BookClub bc " +
            "JOIN BookClubMember bcm ON bc.id = bcm.bookClub.id " +
            "WHERE bcm.member = :member " +
            "AND bc.endDate BETWEEN :now AND :endOfMonth")
    List<BookClub> findMyBookClubs(Member member, LocalDate now, LocalDate endOfMonth);
}
