package com.umc.ttt.domain.review.repository;

import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findByMemberIdAndWriteDate(Long member_id, LocalDate writeDate);

    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE YEAR(r.writeDate) = :year AND MONTH(r.writeDate) = :month AND r.member = :member")
    List<Review> findByMemberAndYearAndMonth(int year, int month, Member member);
}
