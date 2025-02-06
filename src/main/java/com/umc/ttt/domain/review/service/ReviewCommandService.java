package com.umc.ttt.domain.review.service;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.review.dto.ReviewRequestDTO;
import com.umc.ttt.domain.review.dto.ReviewResponseDTO;
import com.umc.ttt.domain.review.entity.Review;
import com.umc.ttt.home.dto.HomeResponseDTO;

import java.util.List;

public interface ReviewCommandService {
    Review addReview(ReviewRequestDTO.AddUpdateDto request, Member member);
    Review updateReview(Long reviewId, ReviewRequestDTO.AddUpdateDto request, Member member);
    List<Review> getReviewCalendar(int year, int month, Member member);
    ReviewResponseDTO.reviewListDTO getReviewList(Long cursor, int limit, Member member);
    Review getReviewInfo(Long reviewId);
    List<HomeResponseDTO.remindReviewDTO> getRandomReviewsByYear();
}