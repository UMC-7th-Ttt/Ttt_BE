package com.umc.ttt.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberAddProfileDTO {

    @NotNull
    private Long memberId;

    @NotNull(message = "닉네임은 필수입니다.")
    private String nickname;

//    private String profileUrl;
}


