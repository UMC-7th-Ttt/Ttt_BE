package com.umc.ttt.domain.home.controller;

import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.global.annotation.CurrentMember;
import com.umc.ttt.global.apiPayload.ApiResponse;
import com.umc.ttt.domain.home.dto.HomeResponseDTO;
import com.umc.ttt.domain.home.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {
    private final HomeService homeService;

    @GetMapping("/")
    @Operation(summary = "홈 화면",description = "홈 화면 API입니다.")
    public ApiResponse<HomeResponseDTO.viewHomeResultDTO> home(@CurrentMember Member member) {
        HomeResponseDTO.viewHomeResultDTO response = homeService.getHomeData(member);
        return ApiResponse.onSuccess(response);
    }

}
