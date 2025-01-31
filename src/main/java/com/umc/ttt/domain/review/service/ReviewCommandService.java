package com.umc.ttt.domain.review.service;

import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.review.dto.ReviewRequestDTO;
import com.umc.ttt.domain.review.entity.Review;

public interface ReviewCommandService {
    public Review addReview(ReviewRequestDTO.AddUpdateDto request, Member member);
}