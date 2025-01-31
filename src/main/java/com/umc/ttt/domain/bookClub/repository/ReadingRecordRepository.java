package com.umc.ttt.domain.bookClub.repository;

import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    Optional<ReadingRecord> findByBookClubMember(BookClubMember bookClubMember);
}
