package com.umc.ttt.domain.review.service;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.repository.BookRepository;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.handler.MemberHandler;
import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.place.repository.PlaceRepository;
import com.umc.ttt.domain.review.converter.ReviewConverter;
import com.umc.ttt.domain.review.dto.ReviewRequestDTO;
import com.umc.ttt.domain.review.dto.ReviewResponseDTO;
import com.umc.ttt.domain.review.entity.Review;
import com.umc.ttt.domain.review.handler.ReviewHandler;
import com.umc.ttt.domain.review.repository.ReviewRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.handler.BookHandler;
import com.umc.ttt.global.apiPayload.exception.handler.PlaceHandler;
import com.umc.ttt.home.converter.HomeConverter;
import com.umc.ttt.home.dto.HomeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReviewCommandServiceImpl implements ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final BookRepository bookRepository;

    // 서평 작성 및 수정
    @Override
    @Transactional
    public Review addReview(ReviewRequestDTO.AddUpdateDto request, Member member) {
        // 서평 생성
        Book book=null;
        Place place=null;
        if(request.getBookId()!=null){
            book = bookRepository.findById(request.getBookId()).orElseThrow(()-> new BookHandler(ErrorStatus.BOOK_NOT_FOUND));
        }

        if(request.getPlaceId()!=null){
            place = placeRepository.findById(request.getPlaceId()).orElseThrow(()-> new PlaceHandler(ErrorStatus.PLACE_NOT_FOUND));
        }

        // 서평에 책이 존재한다면 별점이 1이상 5이하이며 0.5 간격인지 확인
        if(book != null && (request.getBookRanking()<1.0 || request.getBookRanking()>5.0 || request.getBookRanking()%0.5!=0)){
            throw new ReviewHandler(ErrorStatus.INVALID_REVIEW_RANKING);
        }

        // 서평에 장소가 존재한다면 별점이 1이상 5이하이며 0.5 간격인지 확인
        if(place != null && (request.getPlaceRanking()<1.0 || request.getPlaceRanking()>5.0 || request.getPlaceRanking()%0.5!=0)){
            throw new ReviewHandler(ErrorStatus.INVALID_REVIEW_RANKING);
        }
        Review review = ReviewConverter.toReview(request, member, book, place);

        // 공간에 대한 리뷰가 있을 경우, 해당 공간의 평점 계산 후 업데이트
        if (place != null) {
            List<Review> placeReviews = reviewRepository.findAllByPlace(place);

            // 평점 평균 계산
            double averageRating = Stream.concat(
                            placeReviews.stream().mapToDouble(Review::getPlaceRanking).boxed(), // 기존 리뷰들의 평점
                            Stream.of(request.getPlaceRanking()) // 현재 리뷰의 평점
                    )
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0); // 평점이 없으면 0.0으로 처리

            place.updateRating(averageRating);
            placeRepository.save(place);
        }

        // 도서에 대한 리뷰가 있을 경우, 해당 도서의 평점 계산 후 업데이트
        if (book != null) {
            List<Review> bookReviews = reviewRepository.findAllByBook(book);

            // 평점 평균 계산
            double averageBookRating = Stream.concat(
                            bookReviews.stream().mapToDouble(Review::getBookRanking).boxed(), // 기존 리뷰들의 평점
                            Stream.of(request.getBookRanking()) // 현재 리뷰의 평점
                    )
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0); // 평점이 없으면 0.0으로 처리

            book.updateRating(averageBookRating);
            bookRepository.save(book);
        }

        return reviewRepository.save(review);
    }

    // 서평 캘린더 보기
    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewCalendar(int year, int month, Member member) {
        List<Review> reviewList = reviewRepository.findByMemberAndYearAndMonth(year, month, member);
        return reviewList;
    }

    // 서평 모아 보기
    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDTO.reviewListDTO getReviewList(Long cursor, int limit, Member member) {
        Pageable pageable = PageRequest.of(0, limit + 1, Sort.by(Sort.Order.desc("id")));
        Slice<Review> reviews = reviewRepository.findReviewsWithCursor(member, cursor, pageable);

        Long nextCursor = reviews.hasNext() ? reviews.getContent().get(reviews.getContent().size() - 1).getId() : null;

        return ReviewConverter.reviewListDTO(reviews.getContent(), nextCursor, limit, reviews.hasNext());
    }

    // 서평 상세 보기
    @Override
    @Transactional(readOnly = true)
    public Review getReviewInfo(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(()-> new ReviewHandler(ErrorStatus.REVIEW_NOT_FOUND));
    }

    // 홈화면 서평 가져오기 (랜덤 5개)
    @Override
    @Transactional(readOnly = true)
    public List<HomeResponseDTO.remindReviewDTO> getRandomReviewsByYear() {
        Pageable limit = PageRequest.of(0,5);
        List<Review> reviews = reviewRepository.findRandomReviewByYear(limit);

        return reviews.stream()
                .map(HomeConverter::toRemindReviewDTO).collect(Collectors.toList());
    }


}
