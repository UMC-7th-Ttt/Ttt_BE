package com.umc.ttt.domain.review.converter;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.review.dto.ReviewRequestDTO;
import com.umc.ttt.domain.review.dto.ReviewResponseDTO;
import com.umc.ttt.domain.review.entity.Review;

public class ReviewConverter {
    public static ReviewResponseDTO.AddUpdateResultDTO toAddUpdateResultDTO(Review review) {
        return ReviewResponseDTO.AddUpdateResultDTO.builder()
                .reviewId(review.getId())
                .build();
    }

    public static Review toReview(ReviewRequestDTO.AddUpdateDto request, Member member, Book book, Place place) {
        return Review.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .bookRanking(request.getBookRanking())
                .placeRanking(request.getPlaceRanking())
                .writeDate(request.getWriteDate())
                .isSecret(request.isSecret())
                .member(member)
                .book(book)
                .place(place)
                .build();
    }
}
