package com.umc.ttt.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignUpDTO {//자체 로그인 회원가입 API RequestBody / 키워드 나중에 추가할 것
    @NotNull(message = "이메일은 필수입니다.")
    @Email
    private String email;
    
    @NotNull(message = "비밀번호는 필수입니다.")
    private String password;

    private String nickname;
    private String profileUrl;
}


