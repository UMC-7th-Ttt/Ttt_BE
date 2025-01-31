package com.umc.ttt.domain.member.service;

import com.umc.ttt.domain.member.dto.MemberSignUpDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface MemberCommandService {
    void signUp(MemberSignUpDTO memberSignUpDto) throws Exception;

    void signOut(Optional<String> userEmail) throws Exception;

    String refreshAccessToken(String accessToken) throws Exception;

    void isEmailDuplicate(String email) throws Exception;
}
