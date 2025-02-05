package com.umc.ttt.domain.bookClub.repository;

import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookClubMemberRepository extends JpaRepository<BookClubMember, Long> {
    Optional<BookClubMember> findByBookClubAndMember(BookClub bookClub, Member member);

    List<BookClubMember> findByBookClub(BookClub bookClub);

    boolean existsByBookClubAndMember(BookClub bookClub, Member member);

    Long countByBookClubId(Long bookClubId);

    @Query("SELECT bcm FROM BookClub bc " +
            "JOIN BookClubMember bcm ON bc.id = bcm.bookClub.id " +
            "WHERE bcm.member.id = :memberId " +
            "AND bc.startDate <= CURRENT_DATE " +
            "AND bc.endDate >= CURRENT_DATE")
    List<BookClubMember> findActiveBookClubsByMember(Long memberId);
}
