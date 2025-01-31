package com.umc.ttt.global.login.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.ttt.global.apiPayload.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;


/**
 * JWT 로그인 실패 시 처리하는 핸들러
 * SimpleUrlAuthenticationFailureHandler를 상속받아서 구현
 */
@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");


        // 응답 바디에 ApiResponse 추가
        response.setContentType("application/json");
        ApiResponse apiResponse = ApiResponse.onFailure("LOGIN400","로그인에 실패했습니다! 이메일이나 비밀번호를 확인해주세요.",null);

        try {
            ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper 생성
            String jsonResponse = objectMapper.writeValueAsString(apiResponse); // ApiResponse를 JSON으로 변환
            response.getWriter().write(jsonResponse); // 응답 바디에 JSON 작성
        } catch (Exception e) {
            log.error("응답 바디 작성 중 오류 발생", e);
        }
        log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());
    }
}