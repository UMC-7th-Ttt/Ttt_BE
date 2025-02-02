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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        Review review = reviewRepository.findByMemberIdAndWriteDate(member.getId(), request.getWriteDate());

        if(review==null){
            // 서평 생성
            Book book=null;
            Place place=null;
            if(request.getBookId()!=null){
                book = bookRepository.findById(request.getBookId()).orElseThrow(()->new BookHandler(ErrorStatus.BOOK_NOT_FOUND));
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
            review = ReviewConverter.toReview(request, member, book, place);
        }else{
            // 서평 수정
            // db에 저장된 서평에는 책이 존재하지 않고, 저장하려는 서평에 책이 존재하면 setBook
            if(review.getBook()==null && request.getBookId() != null){
                Book book = bookRepository.findById(request.getBookId()).orElseThrow(()-> new MemberHandler(ErrorStatus.BOOK_NOT_FOUND));
                // 별점이 1이상 5이하이며 0.5 간격인지 확인
                if(request.getBookRanking()<1.0 || request.getBookRanking()>5.0 || request.getBookRanking()%0.5!=0){
                    throw new ReviewHandler(ErrorStatus.INVALID_REVIEW_RANKING);
                }
                review.setBook(book, request.getBookRanking());
            }

            // db에 저장된 서평에는 장소가 존재하지 않고, 저장하려는 서평에 장소가 존재하면 setPlace
            if(review.getPlace()==null && request.getPlaceId() != null){
                Place place = placeRepository.findById(request.getPlaceId()).orElseThrow(()-> new MemberHandler(ErrorStatus.PLACE_NOT_FOUND));
                // 별점이 1이상 5이하이며 0.5 간격인지 확인
                if(request.getPlaceRanking()<1.0 || request.getPlaceRanking()>5.0 || request.getPlaceRanking()%0.5!=0){
                    throw new ReviewHandler(ErrorStatus.INVALID_REVIEW_RANKING);
                }
                review.setPlace(place, request.getPlaceRanking());
            }
            if(review.getIsSecret()!=request.getIsSecret()){
                review.setIsSecret(request.getIsSecret());
            }
            if(!request.getTitle().equals(review.getTitle()) || !request.getContent().equals(review.getContent())){
                review.setInfo(request.getTitle(), request.getContent());
            }
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
}
