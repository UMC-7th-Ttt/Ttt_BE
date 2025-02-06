package com.umc.ttt.domain.member.service;

import com.umc.ttt.domain.member.dto.MemberAddProfileDTO;
import com.umc.ttt.domain.member.dto.MemberSignUpDTO;
import com.umc.ttt.domain.member.dto.MemberUpdateInfoDTO;
import com.umc.ttt.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MemberCommandService {
    Member signUp(MemberSignUpDTO memberSignUpDto) throws Exception;

    void signOut(Optional<String> userEmail) throws Exception;

    String refreshAccessToken(String accessToken) throws Exception;

    void isEmailDuplicate(String email) throws Exception;

    void isNicknameDuplicate(String nickname) throws Exception;

    void saveGenreKeyword(Member member, List<String> keywords, Long bookId) throws Exception;

    void saveFormatKeyword(Member member, List<String> keywords) throws Exception;

    List<String> extractTopCategories(List<String> categories) throws Exception;

    Member saveProfile(MemberAddProfileDTO memberProfileDTO, MultipartFile profilePicture) throws Exception;

    Member updateInfo(Member member, MemberUpdateInfoDTO memberUpdateInfoDTO) throws Exception;

    void validatePassword(Member member, String password) throws Exception;
}
