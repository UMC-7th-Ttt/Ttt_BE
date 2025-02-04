package com.umc.ttt.global.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.ttt.global.apiPayload.ApiResponse;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.handler.JwtHandler;
import com.umc.ttt.global.jwt.entity.RefreshToken;
import com.umc.ttt.global.jwt.repository.RefreshTokenRepository;
import com.umc.ttt.global.jwt.service.JwtService;
import com.umc.ttt.global.jwt.util.PasswordUtil;

import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * Jwt 인증 필터
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
 * AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청
 *
 * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지는 않는다.
 * 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 ERROR
 * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급, RefreshToken 재발급(RTR 방식)
 *                              인증 성공 처리는 하지 않고 실패 처리
 *
 */
@RequiredArgsConstructor
@Slf4j
//@EnableWebSecurity(debug = true)
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final Set<String> NO_CHECK_URLS = Set.of("/api/login", "/api/refresh-token","/api/logout");


    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository tokenRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (NO_CHECK_URLS.contains(request.getRequestURI())) {
            log.info("요청 uri: "+request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }
        checkAccessTokenAndAuthentication(request, response, filterChain);

    }

    /**
     * [액세스 토큰 체크 & 인증 처리 메소드]
     * request에서 extractAccessToken()으로 액세스 토큰 추출 후, isTokenValid()로 유효한 토큰인지 검증
     * 유효한 토큰이면, 액세스 토큰에서 extractEmail로 Email을 추출한 후 findByEmail()로 해당 이메일을 사용하는 유저 객체 반환
     * 그 유저 객체를 saveAuthentication()으로 인증 처리하여
     * 인증 허가 처리된 객체를 SecurityContextHolder에 담기
     * 그 후 다음 인증 필터로 진행
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException, JwtHandler {
        log.info("checkAccessTokenAndAuthentication() 호출");

        // Access Token 추출
        jwtService.extractAccessToken(request)
                .map(accessToken -> {
                    if (!jwtService.isTokenValid(accessToken)) {
                        throw new JwtHandler(ErrorStatus.INVALID_TOKEN);
                    }
                    // ✅ Access Token으로 Refresh Token 조회
                    Optional<RefreshToken> refreshTokenOpt = tokenRepository.findByAccessToken(accessToken);
                    if (refreshTokenOpt.isEmpty()) {
                        throw new JwtHandler(ErrorStatus.INVALID_TOKEN); // RefreshToken 없으면 차단
                    }

                    return accessToken; // 유효한 토큰만 다음 단계로 전달
                })
                .ifPresent(accessToken -> jwtService.extractEmail(accessToken)
                        .ifPresent(email -> memberRepository.findByEmail(email)
                                .ifPresent(this::saveAuthentication)));

        // 필터 체인 진행
        filterChain.doFilter(request, response);
    }


    /**
     * [인증 허가 메소드]
     * 파라미터의 유저 : 우리가 만든 회원 객체 / 빌더의 유저 : UserDetails의 User 객체
     *
     * new UsernamePasswordAuthenticationToken()로 인증 객체인 Authentication 객체 생성
     * UsernamePasswordAuthenticationToken의 파라미터
     * 1. 위에서 만든 UserDetailsUser 객체 (유저 정보)
     * 2. credential(보통 비밀번호로, 인증 시에는 보통 null로 제거)
     * 3. Collection < ? extends GrantedAuthority>로,
     * UserDetails의 User 객체 안에 Set<GrantedAuthority> authorities이 있어서 getter로 호출한 후에,
     * new NullAuthoritiesMapper()로 GrantedAuthoritiesMapper 객체를 생성하고 mapAuthorities()에 담기
     *
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
     * setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     */
    public void saveAuthentication(Member myMember) {
        String password = myMember.getPassword();

        if (password == null) { // 소셜 로그인 유저의 비밀번호 임의로 설정 하여 소셜 로그인 유저도 인증 되도록 설정
            password = PasswordUtil.generateRandomPassword();
        }

        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(myMember.getEmail())
                .password(password)
                .roles(myMember.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * shouldNotFilter 메서드를 추가하였습니다. 이 메서드를 이용하면 필터를 거치지 않을 URL 패턴일경우, 필터를 건너 뛰게 됩니다.
     * token/ 을 포함한 요청 URI를 가질경우 필터를 수행하지 않도록 작성했습니다.
     * 기존에 AccessToken의 값이 없는 경우 토큰 검사를 생략하는 조건문을 두었었는데, RefreshToken 재발급이나 로그아웃 기능의 경우, 필터를 수행하면 토큰의 값은 있지만 토큰이 유효하지 않을 수 있기 때문에 이렇게 로직을 변경하였습니다.*/
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().contains("api/token/");
    }

}