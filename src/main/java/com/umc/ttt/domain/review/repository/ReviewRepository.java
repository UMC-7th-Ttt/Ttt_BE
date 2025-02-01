package com.umc.ttt.domain.review.repository;

import com.umc.ttt.domain.review.entity.Review;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    public Review findByMemberIdAndWriteDate(Long member_id, LocalDate writeDate);
}
