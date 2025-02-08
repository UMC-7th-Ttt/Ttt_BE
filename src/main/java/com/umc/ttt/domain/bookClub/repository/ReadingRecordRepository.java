package com.umc.ttt.domain.bookClub.repository;

import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    List<ReadingRecord> findByBookClubMember(BookClubMember bookClubMember);

    List<ReadingRecord> findTop10ByOrderByCreatedAtDesc();

    @Query("""
        SELECT rr FROM ReadingRecord rr
        JOIN rr.bookClubMember bcm
        JOIN bcm.bookClub bc
        WHERE bc.id IN (
            SELECT bcm2.bookClub.id FROM BookClubMember bcm2 
            WHERE bcm2.member.id = :memberId
        )
        AND bcm.member.id != :memberId
        AND (:cursor = 0 OR rr.id < :cursor)
        ORDER BY rr.createdAt DESC
    """)
    Slice<ReadingRecord> findReadingRecordsWithCursor(@Param("memberId") Long memberId, @Param("cursor") Long cursor,
                                                      Pageable pageable);
}
