package com.umc.ttt.domain.review.repository;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.review.entity.Review;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findByMemberIdAndWriteDate(Long member_id, LocalDate writeDate);

    // 서평 캘린더 보기
    @Query(value = "SELECT r " +
            "FROM Review r " +
            "WHERE YEAR(r.writeDate) = :year AND MONTH(r.writeDate) = :month AND r.member = :member AND r.bookRanking <> 0 ")
    List<Review> findByMemberAndYearAndMonth(int year, int month, Member member);

    // 서평 모아보기
    @Query("SELECT r FROM Review r WHERE r.member = :member " +
            "AND r.bookRanking <> 0 " +
            "AND (:cursor = 0 OR r.id < :cursor) " +
            "ORDER BY r.id DESC")
    Slice<Review> findReviewsWithCursor(@Param("member") Member member, @Param("cursor") Long cursor,
                                        Pageable pageable);

    List<Review> findAllByPlace(Place place);

    List<Review> findAllByBook(Book book);

    List<Review> findByBookId(Long bookId);
}