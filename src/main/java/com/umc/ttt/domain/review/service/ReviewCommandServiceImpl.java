package com.umc.ttt.domain.review.service;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.repository.BookRepository;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.handler.MemberHandler;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.place.repository.PlaceRepository;
import com.umc.ttt.domain.review.converter.ReviewConverter;
import com.umc.ttt.domain.review.dto.ReviewRequestDTO;
import com.umc.ttt.domain.review.entity.Review;
import com.umc.ttt.domain.review.repository.ReviewRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewCommandServiceImpl implements ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Review addReview(ReviewRequestDTO.AddUpdateDto request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(()-> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Book book = bookRepository.findById(request.getBookId()).orElse(null);
        Place place = placeRepository.findById(request.getPlaceId()).orElse(null);

        Review review = ReviewConverter.toReview(request, member, book, place);

        return reviewRepository.save(review);
    }
}
