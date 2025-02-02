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

    // 서평 모아보기
    public static ReviewResponseDTO.reviewDTO reviewDTO(Review review) {
        return ReviewResponseDTO.reviewDTO.builder()
                .id(review.getId())
                .cover(review.getBook().getCover())
                .build();
    }

    public static ReviewResponseDTO.reviewListDTO reviewListDTO(List<Review> reviewList, Long nextCursor, int limit, boolean hasNext) {
        List<ReviewResponseDTO.reviewDTO> reviewDTOList = reviewList.stream()
                .map(ReviewConverter::reviewDTO).collect(Collectors.toList());

        return ReviewResponseDTO.reviewListDTO.builder()
                .reviewList(reviewDTOList)
                .nextCursor(nextCursor)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }

    // 서평 상세 보기
    public static ReviewResponseDTO.bookDTO bookDTO(Book book) {
        return ReviewResponseDTO.bookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .cover(book.getCover())
                .build();
    }

    public static ReviewResponseDTO.placeDTO placeDTO(Place place) {
        String address = null;
        if(place.getAddress() != null) {
            address = place.getAddress().split(" ")[0];
        }

        return ReviewResponseDTO.placeDTO.builder()
                .id(place.getId())
                .title(place.getTitle())
                .address(address)
                .image(place.getImage())
                .build();
    }

    public static ReviewResponseDTO.reviewInfoDTO reviewInfoDTO(Review review) {
        if(review.getBookRanking()==0&&review.getPlaceRanking()==0) {   // 서평에 책, 장소 평점 작성을 안 함.
            return ReviewResponseDTO.reviewInfoDTO.builder()
                    .id(review.getId())
                    .title(review.getTitle())
                    .content(review.getContent())
                    .isSecret(review.getIsSecret())
                    .bookRanking(review.getBookRanking())
                    .placeRanking(review.getPlaceRanking())
                    .writeDate(review.getWriteDate())
                    .build();
        }else if(review.getBookRanking()==0){   // 서평에 책 평점 작성을 안 함.
            return ReviewResponseDTO.reviewInfoDTO.builder()
                    .id(review.getId())
                    .title(review.getTitle())
                    .content(review.getContent())
                    .isSecret(review.getIsSecret())
                    .bookRanking(review.getBookRanking())
                    .placeRanking(review.getPlaceRanking())
                    .writeDate(review.getWriteDate())
                    .place(ReviewConverter.placeDTO(review.getPlace()))
                    .build();
        }else if(review.getPlaceRanking()==0){  // 서평에 장소 평점 작성을 안 함.
            return ReviewResponseDTO.reviewInfoDTO.builder()
                    .id(review.getId())
                    .title(review.getTitle())
                    .content(review.getContent())
                    .isSecret(review.getIsSecret())
                    .bookRanking(review.getBookRanking())
                    .placeRanking(review.getPlaceRanking())
                    .writeDate(review.getWriteDate())
                    .book(ReviewConverter.bookDTO(review.getBook()))
                    .build();
        }else{  // 서평에 책, 장소 평점을 작성함.
            return ReviewResponseDTO.reviewInfoDTO.builder()
                    .id(review.getId())
                    .title(review.getTitle())
                    .content(review.getContent())
                    .isSecret(review.getIsSecret())
                    .bookRanking(review.getBookRanking())
                    .placeRanking(review.getPlaceRanking())
                    .writeDate(review.getWriteDate())
                    .book(ReviewConverter.bookDTO(review.getBook()))
                    .place(ReviewConverter.placeDTO(review.getPlace()))
                    .build();
        }


    }
}
