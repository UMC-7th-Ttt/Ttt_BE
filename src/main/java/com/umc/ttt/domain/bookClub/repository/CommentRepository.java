package com.umc.ttt.domain.bookClub.repository;

import com.umc.ttt.domain.bookClub.entity.Comment;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByReadingRecordAndParentIsNull(ReadingRecord readingRecord);
}
