package com.umc.ttt.domain.review.service;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.repository.BookRepository;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.handler.MemberHandler;
import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.place.repository.PlaceRepository;
import com.umc.ttt.domain.review.converter.ReviewConverter;
import com.umc.ttt.domain.review.dto.ReviewRequestDTO;
import com.umc.ttt.domain.review.entity.Review;
import com.umc.ttt.domain.review.handler.ReviewHandler;
import com.umc.ttt.domain.review.repository.ReviewRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.handler.BookHandler;
import com.umc.ttt.global.apiPayload.exception.handler.PlaceHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewCommandServiceImpl implements ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Review addReview(ReviewRequestDTO.AddUpdateDto request, Member member) {
        Review review = reviewRepository.findByMemberIdAndWriteDate(member.getId(), request.getWriteDate());

        if(review==null){
            // 리뷰 생성
            Book book=null;
            Place place=null;
            if(request.getBookId()!=null){
                book = bookRepository.findById(request.getBookId()).orElseThrow(()->new BookHandler(ErrorStatus.BOOK_NOT_FOUND));
            }

            if(request.getPlaceId()!=null){
                place = placeRepository.findById(request.getPlaceId()).orElseThrow(()-> new PlaceHandler(ErrorStatus.PLACE_NOT_FOUND));
            }

            // 리뷰에 책이 존재한다면 별점이 1이상 5이하이며 0.5 간격인지 확인
            if(book != null && (request.getBookRanking()<1.0 || request.getBookRanking()>5.0 || request.getBookRanking()%0.5!=0)){
                throw new ReviewHandler(ErrorStatus.INVALID_REVIEW_RANKING);
            }

            // 장소에 책이 존재한다면 별점이 1이상 5이하이며 0.5 간격인지 확인
            if(place != null && (request.getPlaceRanking()<1.0 || request.getPlaceRanking()>5.0 || request.getPlaceRanking()%0.5!=0)){
                throw new ReviewHandler(ErrorStatus.INVALID_REVIEW_RANKING);
            }
            review = ReviewConverter.toReview(request, member, book, place);
        }else{
            // 리뷰 수정
            // db에 저장된 리뷰에는 책이 존재하지 않고, 저장하려는 리뷰에 책이 존재하면 setBook
            if(review.getBook()==null && request.getBookId() != null){
                Book book = bookRepository.findById(request.getBookId()).orElseThrow(()-> new MemberHandler(ErrorStatus.BOOK_NOT_FOUND));
                // 별점이 1이상 5이하이며 0.5 간격인지 확인
                if(request.getBookRanking()<1.0 || request.getBookRanking()>5.0 || request.getBookRanking()%0.5!=0){
                    throw new ReviewHandler(ErrorStatus.INVALID_REVIEW_RANKING);
                }
                review.setBook(book, request.getBookRanking());
            }

            // db에 저장된 리뷰에는 장소가 존재하지 않고, 저장하려는 리뷰에 장소가 존재하면 setPlace
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

}
