package com.umc.ttt.global.login.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.global.apiPayload.ApiResponse;
import com.umc.ttt.global.jwt.entity.GeneratedToken;
import com.umc.ttt.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;


@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String email = extractUsername(authentication); // 인증 정보에서 Username(email) 추출
        GeneratedToken generatedToken = jwtService.generateToken(email);
        
        String accessToken = generatedToken.getAccessToken();
        String refreshToken = generatedToken.getRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 헤더에 AccessToken, RefreshToken 실어서 응답

        //db 저장X redis 에 저장
//        memberRepository.findByEmail(email)
//                .ifPresent(user -> {
//                    user.updateRefreshToken(refreshToken);//db에 저장
//                    memberRepository.saveAndFlush(user);
//                });
        log.info("로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
        log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);

        // 응답 바디에 ApiResponse 추가
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ApiResponse apiResponse = ApiResponse.onSuccess("로그인에 성공했습니다!");

        try {
            ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper 생성
            String jsonResponse = objectMapper.writeValueAsString(apiResponse); // ApiResponse를 JSON으로 변환
            response.getWriter().write(jsonResponse); // 응답 바디에 JSON 작성
        } catch (Exception e) {
            log.error("응답 바디 작성 중 오류 발생", e);
        }
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}