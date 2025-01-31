package com.umc.ttt.global.oauth2.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.ttt.domain.member.entity.enums.Role;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.global.apiPayload.ApiResponse;
import com.umc.ttt.global.jwt.entity.GeneratedToken;
import com.umc.ttt.global.jwt.service.JwtService;
import com.umc.ttt.global.oauth2.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성

        } catch (Exception e) {
            throw e;
        }

    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String email = oAuth2User.getEmail();

        GeneratedToken generatedToken = jwtService.generateToken(email);

        String accessToken = generatedToken.getAccessToken();
        String refreshToken = generatedToken.getRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
//        jwtService.updateRefreshToken(email, refreshToken);

        log.info("구글로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("구글로그인에 성공하였습니다. AccessToken : {}", accessToken);
        log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);

        // 응답 바디에 ApiResponse 추가
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ApiResponse apiResponse = ApiResponse.onSuccess("소셜로그인에 성공했습니다!");

        try {
            ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper 생성
            String jsonResponse = objectMapper.writeValueAsString(apiResponse); // ApiResponse를 JSON으로 변환
            response.getWriter().write(jsonResponse); // 응답 바디에 JSON 작성
        } catch (Exception e) {
            log.error("응답 바디 작성 중 오류 발생", e);
        }
    }
}
