package com.umc.ttt.domain.review.converter;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.review.dto.ReviewRequestDTO;
import com.umc.ttt.domain.review.dto.ReviewResponseDTO;
import com.umc.ttt.domain.review.entity.Review;

import java.util.List;
import java.util.stream.Collectors;

// 서평 작성
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
                .isSecret(request.getIsSecret())
                .member(member)
                .book(book)
                .place(place)
                .build();
    }

    // 서평 캘린더 보기
    public static ReviewResponseDTO.reviewCalendarDTO reviewCalendarDTO(Review review) {
        return ReviewResponseDTO.reviewCalendarDTO.builder()
                .id(review.getId())
                .cover(review.getBook().getCover())
                .writeDate(review.getWriteDate())
                .build();
    }

    public static ReviewResponseDTO.reviewCalendarListDTO reviewCalendarListDTO(List<Review> reviewList) {
        List<ReviewResponseDTO.reviewCalendarDTO> reviewCalendarDTOList = reviewList.stream()
                .map(ReviewConverter::reviewCalendarDTO).collect(Collectors.toList());

        return ReviewResponseDTO.reviewCalendarListDTO.builder()
                .reviewList(reviewCalendarDTOList)
                .build();
    }
}
