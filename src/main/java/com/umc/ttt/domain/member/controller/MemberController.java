package com.umc.ttt.domain.member.controller;

import com.umc.ttt.domain.member.converter.MemberConverter;
import com.umc.ttt.domain.member.dto.MemberKeywordDTO;
import com.umc.ttt.domain.member.dto.MemberResponseDTO;
import com.umc.ttt.domain.member.dto.MemberSignUpDTO;
import com.umc.ttt.domain.member.dto.TokenResponseDTO;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.service.MemberCommandService;
import com.umc.ttt.global.annotation.CurrentMember;
import com.umc.ttt.global.apiPayload.ApiResponse;
import com.umc.ttt.global.jwt.repository.RefreshTokenRepository;
import com.umc.ttt.global.jwt.service.JwtService;
import com.umc.ttt.global.jwt.service.RefreshTokenService;
import com.umc.ttt.global.oauth2.service.GoogleIdTokenVerify;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberCommandService memberCommandService;
    private final JwtService jwtService;
    private final RefreshTokenService tokenService;
    private final RefreshTokenRepository tokenRepository;
    private final GoogleIdTokenVerify googleIdTokenVerify;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "회원가입 API입니다. ")
    public ApiResponse<MemberResponseDTO.MemberInfoDTO> signUp(@RequestBody @Valid MemberSignUpDTO memberSignUpDTO) throws Exception {
        Member member = memberCommandService.signUp(memberSignUpDTO);
        return ApiResponse.onSuccess(MemberConverter.toMemberInfoDTO(member));
    }


    @DeleteMapping("/sign-out")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 시 사용하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER401", description = "사용자가 없습니다", content = @Content(mediaType = "application/json")),
    })
    public ApiResponse<String> signOut(@RequestHeader("Authorization") String token) throws Exception {
        String jwtToken = token.substring(7);
        tokenService.removeRefreshToken(jwtToken);

        var userEmail = jwtService.extractEmail(jwtToken);
        memberCommandService.signOut(userEmail);
        return ApiResponse.onSuccess("회원 탈퇴에 성공했습니다!");
    }


    //JWT 서비스 테스트를 위한 API
    @GetMapping("/jwt-test")
    @Operation(summary = "jwtTest 요청", description = "서버 테스트용 api입니다. 연동x")
    public ApiResponse<String> jwtTest() {
        return ApiResponse.onSuccess("jwtTest 요청 성공");
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃",description = "로그아웃 하는 API입니다.accessToken 필요")
    public ApiResponse<String> logout(@RequestHeader("Authorization") String accessToken) {
        // accessToken으로 현재 Redis 정보 삭제
        String jwtToken = accessToken.substring(7);
        tokenService.removeRefreshToken(jwtToken);
        return ApiResponse.onSuccess("로그아웃에 성공하였습니다.");
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "액세스 토큰 재발급", description = "accessToken 재발급하는 API입니다.accessToken 필요")
    public ApiResponse<TokenResponseDTO.UpdateResultDTO> refresh(@RequestHeader("Authorization") String accessToken) throws Exception {
        String jwtToken = accessToken.substring(7); // "Bearer " 제거
        log.info("AccessToken: {}", jwtToken);

        String newAccessToken = memberCommandService.refreshAccessToken(jwtToken);
        return ApiResponse.onSuccess(MemberConverter.updateResultDTO(newAccessToken));
    }

    @PostMapping("/email-duplicated/{email}")
    @Operation(summary = "이메일 중복 검사", description = "")
    public ApiResponse<String> duplicatedEmail(@PathVariable(name = "email") @Email String email) throws Exception {
        memberCommandService.isEmailDuplicate(email);
        return ApiResponse.onSuccess("사용가능한 이메일입니다.");
    }
    @PostMapping("/nickname-duplicated/{nickname}")
    @Operation(summary = "닉네임 중복 검사", description = "")
    public ApiResponse<String> duplicatedNickname(@PathVariable(name = "nickname") String nickname) throws Exception {
        memberCommandService.isNicknameDuplicate(nickname);
        return ApiResponse.onSuccess("사용가능한 닉네임입니다.");
    }

    //소셜 회원가입/로그인
    // TODO idToken 테스트 , 구글에 요청 잘 되는지 확인해야함.
    @PostMapping("/google-login")
    @Operation(summary = "소셜 회원가입/로그인", description = "테스트 : idtoken 테스트 후 회원가입/로그인 진행. ")
    public ResponseEntity<?> validateToken(@RequestBody String idToken) {
        try {
            Map<String, Object> userDetails = googleIdTokenVerify.authenticateUser(idToken);
            return ResponseEntity.ok(userDetails);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error validating the ID token: " + e.getMessage());
        }
    }
    
    
    //키워드 저장
    @PostMapping("/users/keyword/{memberId}")
    public ApiResponse<String> savePreferredCategories(@PathVariable(name = "memberId") Long memberId, @Valid @RequestBody MemberKeywordDTO requestDTO) throws Exception {
        memberCommandService.saveGenreKeyword(memberId, requestDTO.getPreferCategory1(), requestDTO.getPreferBookId());
        memberCommandService.saveFormatKeyword(memberId, requestDTO.getPreferCategory2());
        return ApiResponse.onSuccess("선호 카테고리 저장 완료");
    }
}
