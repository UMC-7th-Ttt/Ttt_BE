package com.umc.ttt.domain.member.service;

import com.umc.ttt.domain.member.dto.MemberSignUpDTO;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.entity.enums.ProviderType;
import com.umc.ttt.domain.member.entity.enums.Role;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.GeneralException;
import com.umc.ttt.global.apiPayload.exception.handler.JwtHandler;
import com.umc.ttt.global.apiPayload.exception.handler.MemberHandler;
import com.umc.ttt.global.jwt.entity.RefreshToken;
import com.umc.ttt.global.jwt.repository.RefreshTokenRepository;
import com.umc.ttt.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository tokenRepository;

    @Override
    public void signUp(MemberSignUpDTO memberSignUpDto) throws Exception {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberSignUpDto.getEmail());

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (member.getProviderType() == ProviderType.GOOGLE) {
                throw new Exception("이미 존재하는 이메일입니다. 구글 로그인으로 로그인해주세요.");
            } else {
                throw new Exception("이미 존재하는 이메일입니다.");
            }
        }

        if (memberRepository.findByNickname(memberSignUpDto.getNickname()).isPresent()) {
            throw new Exception("이미 존재하는 닉네임입니다.");
        }

        Member member = Member.builder()
                .email(memberSignUpDto.getEmail())
                .password(memberSignUpDto.getPassword())
                .nickname(memberSignUpDto.getNickname())
                .profileUrl(memberSignUpDto.getProfileUrl())
                .providerType(ProviderType.EMAIL)
                .role(Role.GUEST)
                .build();

        member.passwordEncode(passwordEncoder);
        memberRepository.save(member);
    }

    @Override
    public void signOut(Optional<String> userEmail) throws Exception {
        log.info("회원탈퇴 이메일 : {}", userEmail.orElse("이메일 없음"));

        String email = userEmail.get();
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            memberRepository.deleteById(member.get().getId());
        } else {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }
    }

    @Override
    public String refreshAccessToken(String accessToken) throws JwtHandler {
        // 액세스 토큰으로 Refresh 토큰 객체를 조회
        Optional<RefreshToken> refreshTokenOpt = tokenRepository.findByAccessToken(accessToken);

        if (refreshTokenOpt.isEmpty()) {
            throw new JwtHandler(ErrorStatus.INVALID_TOKEN);
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        // RefreshToken 검증
        if (!jwtService.isTokenValid(refreshToken.getRefreshToken())) {
            throw new JwtHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        // 새 AccessToken 생성
        String newAccessToken = jwtService.generateAccessToken(refreshToken.getId());

        // AccessToken 업데이트
        refreshToken.updateAccessToken(newAccessToken);
        tokenRepository.save(refreshToken);

        return newAccessToken;
    }

    @Override
    public void isEmailDuplicate(String email) throws MemberHandler {
        log.info("여기1");
        if (memberRepository.findByEmail(email).isPresent()) {
            log.info("여기2");
            throw new MemberHandler(ErrorStatus.MEMBER_ALREADY_EXISTS);
        }
    }
}
