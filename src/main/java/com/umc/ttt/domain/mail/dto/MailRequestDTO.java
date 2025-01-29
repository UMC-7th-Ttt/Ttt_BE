package com.umc.ttt.domain.mail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class MailRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Mail {
        @Email
        @NotNull(message = "이메일은 필수입니다.")
        private String email;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MailVerificationRequest {

        @Email
        @NotNull(message = "이메일은 필수입니다.")
        private String email;

        @NotNull(message = "인증코드 필수입니다.")
        @Size(min = 6, max = 6)
        private String code;
    }
}
