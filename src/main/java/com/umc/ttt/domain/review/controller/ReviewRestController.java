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

    @PutMapping("/")
    @Operation(summary = "서평 작성 및 수정",description = "작성한 서평을 저장 및 수정하는 API입니다.")
    public ApiResponse<ReviewResponseDTO.AddUpdateResultDTO> add(@RequestBody @Valid ReviewRequestDTO.AddUpdateDto request, @CurrentMember Member member) {
        Review review = reviewCommandService.addReview(request, member);
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
}
