package com.umc.ttt.domain.review.repository;

import com.umc.ttt.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
