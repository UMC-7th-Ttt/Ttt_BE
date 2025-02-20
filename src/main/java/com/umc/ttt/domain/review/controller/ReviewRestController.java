package com.umc.ttt.domain.review.controller;

import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.domain.review.converter.ReviewConverter;
import com.umc.ttt.domain.review.dto.ReviewRequestDTO;
import com.umc.ttt.domain.review.dto.ReviewResponseDTO;
import com.umc.ttt.domain.review.entity.Review;
import com.umc.ttt.domain.review.service.ReviewCommandService;
import com.umc.ttt.global.annotation.CurrentMember;
import com.umc.ttt.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewRestController {
    private final ReviewCommandService reviewCommandService;

    @PostMapping("/")
    @Operation(summary = "서평 작성",description = "작성한 서평을 저장하는 API입니다.")
    public ApiResponse<ReviewResponseDTO.AddUpdateResultDTO> addReview(@RequestBody @Valid ReviewRequestDTO.AddUpdateDto request, @CurrentMember Member member) {
        Review review = reviewCommandService.addReview(request, member);
        return ApiResponse.onSuccess(ReviewConverter.toAddUpdateResultDTO(review));
    }

    @PatchMapping("/{reviewId}")
    @Operation(summary = "서평 수정", description = "서평을 수정하는 API입니다.")
    public ApiResponse<ReviewResponseDTO.AddUpdateResultDTO> updateReview(@PathVariable(name = "reviewId") Long reviewId,
                                                                          @RequestBody @Valid ReviewRequestDTO.AddUpdateDto request,
                                                                          @CurrentMember Member member) {
        Review review = reviewCommandService.updateReview(reviewId, request, member);
        return ApiResponse.onSuccess(ReviewConverter.toAddUpdateResultDTO(review));
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "서평 삭제", description = "서평을 삭제하는 API입니다.")
    public ApiResponse<Void> deleteReview(@PathVariable(name = "reviewId") Long reviewId){
        reviewCommandService.deleteReview(reviewId);
        return ApiResponse.onSuccess(null);
    }

    // 서평 책 삭제
    @DeleteMapping("/{reviewId}/book")
    @Operation(summary = "책 리뷰 삭제", description = "책 리뷰를 삭제하는 API입니다.")
    public ApiResponse<ReviewResponseDTO.AddUpdateResultDTO> deleteBookReview(@PathVariable(name = "reviewId") Long reviewId){
        Review review = reviewCommandService.deleteBookReview(reviewId);
        return ApiResponse.onSuccess(ReviewConverter.toAddUpdateResultDTO(review));
    }

    // 서평 도서 삭제
    @DeleteMapping("/{reviewId}/place")
    @Operation(summary = "장소 리뷰 삭제", description = "장소 리뷰를 삭제하는 API입니다.")
    public ApiResponse<ReviewResponseDTO.AddUpdateResultDTO> deletePlaceReview(@PathVariable(name = "reviewId") Long reviewId){
        Review review = reviewCommandService.deletePlaceReview(reviewId);
        return ApiResponse.onSuccess(ReviewConverter.toAddUpdateResultDTO(review));
    }

    // 서평 책 수정
    @PatchMapping("/{reviewId}/book")
    @Operation(summary = "책 별점 수정(별점 삭제 후 추가할 때)", description = "책 별점을 수정하는 API입니다.")
    public ApiResponse<ReviewResponseDTO.AddUpdateResultDTO> updateReview(@PathVariable(name = "reviewId") Long reviewId,
                                                                          @RequestBody @Valid ReviewRequestDTO.AddUpdateBookReviewDto request) {
        Review review = reviewCommandService.updateBookReview(reviewId, request);
        return ApiResponse.onSuccess(ReviewConverter.toAddUpdateResultDTO(review));
    }

    // 서평 장소 수정
    @PatchMapping("/{reviewId}/place")
    @Operation(summary = "장소 별점 수정(별점 삭제 후 추가할 때)", description = "장소 별점을 수정하는 API입니다.")
    public ApiResponse<ReviewResponseDTO.AddUpdateResultDTO> updateReview(@PathVariable(name = "reviewId") Long reviewId,
                                                                          @RequestBody @Valid ReviewRequestDTO.AddUpdatePlaceReviewDto request) {
        Review review = reviewCommandService.updatePlaceReview(reviewId, request);
        return ApiResponse.onSuccess(ReviewConverter.toAddUpdateResultDTO(review));
    }


    @GetMapping("/calendar")
    @Operation(summary = "서평 보기-캘린더",description = "캘린더로 작성한 서평을 조회하는 API입니다.")
    public ApiResponse<ReviewResponseDTO.reviewCalendarListDTO> viewCalendarReview(@RequestParam int year, @RequestParam int month, @CurrentMember Member member) {
        List<Review> reviewList = reviewCommandService.getReviewCalendar(year, month, member);
        return ApiResponse.onSuccess(ReviewConverter.reviewCalendarListDTO(reviewList));
    }

    @GetMapping("/")
    @Operation(summary = "서평 보기- 모아보기",description = "작성한 서평을 모아보는 API입니다.\n\n" +
            "첫 페이지 조회 시 cursor 값으로 0을 전달해주세요.\n\n" +
            "첫 페이지가 아닌 경우 이전 응답의 hasNext가 true일 때, nextCursor 값을 cursor로 전달해주세요.")
    public ApiResponse<ReviewResponseDTO.reviewListDTO> viewReviewList(@RequestParam(name = "cursor", defaultValue = "0") Long cursor,
                                                                       @RequestParam(name = "limit", defaultValue = "10") int limit,
                                                                       @CurrentMember Member member) {
        ReviewResponseDTO.reviewListDTO response = reviewCommandService.getReviewList(cursor, limit, member);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "서평 상세 보기",description = "서평을 상세 조회하는 API입니다.")
    public ApiResponse<ReviewResponseDTO.reviewInfoDTO> viewReviewInfo(@PathVariable Long reviewId, @CurrentMember Member member) {
        Review review = reviewCommandService.getReviewInfo(reviewId);
        return ApiResponse.onSuccess(ReviewConverter.reviewInfoDTO(review,member));
    }
}
