package com.umc.ttt.domain.member.entity;

import com.umc.ttt.domain.member.entity.enums.MemberStatus;
import com.umc.ttt.domain.member.entity.enums.ProviderType;
import com.umc.ttt.domain.member.entity.enums.Role;
import com.umc.ttt.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String email;   // 이메일

    private String password;    // 비밀번호

    private String nickname;    // 닉네임

    private String profileUrl;  // 프로필 이미지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // 역할(USER, ADMIN)

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;  // 로그인 타입(EMAIL, GOOGLE)


    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    private MemberStatus status; // 활동/ 비활동(ACTIVE, INACTIVE)

    private String refreshToken; // 리프레시 토큰

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = Role.USER;
    }

    // 비밀번호 암호화 메소드
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
    //== 정보 수정 ==//
    public void updatePassword(PasswordEncoder passwordEncoder, String password){
        this.password = passwordEncoder.encode(password);
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<MemberPreferedCategory> preferedCategories;

}
