package com.umc.ttt.domain.bookClub.repository;

import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookClubMemberRepository extends JpaRepository<BookClubMember, Long> {
    Optional<BookClubMember> findByBookClubAndMember(BookClub bookClub, Member member);

    List<BookClubMember> findByBookClub(BookClub bookClub);

    boolean existsByBookClubAndMember(BookClub bookClub, Member member);
}
