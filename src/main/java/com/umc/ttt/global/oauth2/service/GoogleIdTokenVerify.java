package com.umc.ttt.global.oauth2.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.entity.enums.ProviderType;
import com.umc.ttt.domain.member.entity.enums.Role;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.handler.MemberHandler;
import com.umc.ttt.global.jwt.entity.GeneratedToken;
import com.umc.ttt.global.jwt.service.JwtService;
import com.umc.ttt.global.oauth2.OAuthAttributes;
import com.umc.ttt.global.oauth2.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleIdTokenVerify {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Value("${app.google.client.id}")
    private String googleClientId;


    public Map<String, Object> authenticateUser(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken != null) {
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String userId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            log.info(email);


            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("socialId", userId);
            userDetails.put("email", email);
            userDetails.put("nickname", name);
            userDetails.put("pictureUrl", pictureUrl);

            Member createdUser = getUser(userDetails); // getUser() 메소드로 User 객체 생성 후 반환
            GeneratedToken generatedToken = jwtService.generateToken(email);

            String accessToken = generatedToken.getAccessToken();

            userDetails.put("accessToken", accessToken);

            return userDetails;
        } else {
            throw new IllegalArgumentException("Invalid ID token.");
        }
    }

    private Member getUser(Map<String, Object> userDetails) throws MemberHandler {

        Member findMember = memberRepository.findByEmail((String) userDetails.get("email")).orElse(null);

        if(findMember == null) {
            return saveUser(userDetails);//회원가입.
        }else if(findMember.getProviderType().equals(ProviderType.EMAIL)) {
            throw new OAuth2AuthenticationException(ErrorStatus.MEMBER_ALREADY_EXISTS.getCode());
        }
        return findMember;
    }

    /**회원가입**/
    private Member saveUser(Map<String, Object> userDetails) {
        Member createdUser = Member.builder()
                .providerType(ProviderType.GOOGLE)
                .socialId(userDetails.get("socialId").toString())
                .email(userDetails.get("email").toString())
                .nickname(userDetails.get("nickname").toString())
                .profileUrl(userDetails.get("pictureUrl").toString())
                .role(Role.GUEST)
                .build();

        return memberRepository.save(createdUser);
    }
}
