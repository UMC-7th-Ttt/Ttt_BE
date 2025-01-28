package com.umc.ttt.domain.mail.controller;

import com.umc.ttt.domain.mail.dto.MailRequestDTO;
import com.umc.ttt.domain.mail.service.MailService;
import com.umc.ttt.domain.member.service.MemberCommandService;
import com.umc.ttt.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@EnableAsync
public class MailController {

    private final MailService mailService;
    private final MemberCommandService userService;

    /**
     * 인증번호 발송 메소드
     */
    @PostMapping("/api/users/mail")
    @Operation(summary = "인증번호 요청", description = "인증번호 요청 api입니다. request의 담긴 email로 발송됩니다.")
    public ApiResponse<String> mailSend(@RequestBody @Valid MailRequestDTO.Mail mailRequest) {
        mailService.sendMail(mailRequest.getEmail());
        return ApiResponse.onSuccess("인증번호가 발송되었습니다.");
    }

    /**
     * 인증번호 검증 메소드
     */
    @PostMapping("/api/users/verify-code")
    @Operation(summary = "인증번호 검증", description = "인증번호 검증 api입니다. 해당 이메일에 발송된 인증번호가 맞는지 검사합니다.")
    public ApiResponse<String> verifyCode(@RequestBody @Valid MailRequestDTO.MailVerificationRequest verificationRequest) {
        mailService.verifyCode(verificationRequest.getEmail(), verificationRequest.getCode());
        return ApiResponse.onSuccess("본인 인증되었습니다.");
    }
}
