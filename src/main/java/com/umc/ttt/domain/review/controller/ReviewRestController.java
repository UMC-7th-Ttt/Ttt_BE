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
}
