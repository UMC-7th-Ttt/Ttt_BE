package com.umc.ttt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateInfoDTO {
    private String nickname;

    private String profileUrl;

    private String password;
}


