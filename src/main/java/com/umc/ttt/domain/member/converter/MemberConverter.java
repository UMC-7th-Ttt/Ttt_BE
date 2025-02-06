package com.umc.ttt.domain.member.converter;
import com.umc.ttt.domain.member.dto.TokenResponseDTO;
import com.umc.ttt.domain.member.dto.MemberResponseDTO;
import com.umc.ttt.domain.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

public class MemberConverter {

    public static List<MemberResponseDTO.MemberInfoDTO> toMemberInfoListDTO(List<Member> members) {
        return members.stream()
                .map(MemberConverter::toMemberInfoDTO)
                .collect(Collectors.toList());
    }

    public static MemberResponseDTO.MemberInfoDTO toMemberInfoDTO(Member member) {
        return MemberResponseDTO.MemberInfoDTO.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileUrl(member.getProfileUrl())
                .build();
    }

    public static MemberResponseDTO.MemberProfileDTO2 toMemberProfileDTO(Member member) {
        return MemberResponseDTO.MemberProfileDTO2.builder()
                .createdAt(member.getCreatedAt())
                .id(member.getId())
                .nickname(member.getNickname())
                .profileUrl(member.getProfileUrl())
                .role(member.getRole())
                .build();
    }

    public static MemberResponseDTO.MemberProfileDTO toMemberProfileDTO(Member member, String accessToken) {
        return MemberResponseDTO.MemberProfileDTO.builder()
                .createdAt(member.getCreatedAt())
                .id(member.getId())
                .nickname(member.getNickname())
                .profileUrl(member.getProfileUrl())
                .role(member.getRole())
                .accessToken(accessToken)
                .build();
    }




    public static TokenResponseDTO.UpdateResultDTO updateResultDTO(String token) {
        return TokenResponseDTO.UpdateResultDTO.builder()
                .accessToken(token)
                .build();
    }

}
