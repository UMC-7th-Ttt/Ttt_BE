package com.umc.ttt.domain.member.dto;

import com.umc.ttt.domain.member.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoDTO {
        Long id;
        String nickname;
        String profileUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberProfileDTO {
        LocalDateTime createdAt;
        Long id;
        String nickname;
        String profileUrl;
        Role role;
        String accessToken;
    }
}