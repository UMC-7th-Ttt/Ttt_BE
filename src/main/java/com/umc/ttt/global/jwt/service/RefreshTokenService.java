package com.umc.ttt.global.jwt.service;

import com.umc.ttt.global.jwt.repository.RefreshTokenRepository;
import com.umc.ttt.global.jwt.entity.RefreshToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Transactional
    public void saveTokenInfo(String email, String refreshToken, String accessToken) {
        repository.save(new RefreshToken(email, accessToken, refreshToken));
    }

    @Transactional
    public void removeRefreshToken(String accessToken) {
        RefreshToken token = repository.findByAccessToken(accessToken)
                .orElseThrow(IllegalArgumentException::new);
        repository.delete(token);
    }
}