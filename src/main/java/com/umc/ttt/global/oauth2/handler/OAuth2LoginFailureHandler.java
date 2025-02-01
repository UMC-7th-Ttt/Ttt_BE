package com.umc.ttt.global.oauth2.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.handler.JwtHandler;
import com.umc.ttt.global.apiPayload.exception.handler.MemberHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper =new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write("소셜 로그인 실패" + " 이미 가입한 사용자입니다.");
        log.info("소셜 로그인에 실패했습니다. 에러 메시지 : {}", exception.getMessage());

        MemberHandler e = new MemberHandler(ErrorStatus.MEMBER_ALREADY_EXISTS);
        objectMapper.writeValue(response.getWriter(), e.getErrorReasonHttpStatus());
    }
}