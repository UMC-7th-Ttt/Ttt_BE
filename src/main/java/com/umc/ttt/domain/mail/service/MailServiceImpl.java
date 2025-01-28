package com.umc.ttt.domain.mail.service;

import com.umc.ttt.domain.mail.handler.EMailHandler;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.redis.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * 1) 주어진 이메일 주소에 대해 6자리 인증 코드를 생성하고 verificationCodes 맵에 저장한다. {이메일 : 인증코드} 형태로 저장될 것이다.
 * 2) 입력한 이메일 주소로 발송할 이메일 메시지를 작성한다.
 * 3) 2에서 생성한 이메일 메시지를 비동기적으로 발송한다.
 * 4) 사용자가 입력한 인증코드와 실제 발송된 인증코드와 일치하는지 확인한다.
 **/

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final MemberRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private static final Map<String, Integer> verificationCodes = new HashMap<>();
    private final RedisService redisService;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    //auth-code-expiration-millis: 이메일 인증 코드의 만료 시간(Millisecond)
    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    /**
     * 인증 코드 자동 생성 메서드
     */
    private String createCode() {
        int lenth = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lenth; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.debug("MemberService.createCode() exception occur");
        }

        return null;
    }

    /**
     * 이메일 전송
     */
    @Override
    public MimeMessage createMail(String mail, String code){
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(mail);
            helper.setSubject("Ttt 이메일 인증번호");
            String body = "<h2>Ttt에 오신걸 환영합니다!</h2><h3>아래의 인증번호를 입력하세요.</h3><h1>" + code + "</h1><h3>감사합니다.</h3>";
            helper.setText(body, true);


        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    /**
     * createMail() 메서드의 내용을 이메일 전송
     */
    @Async
    @Override
    public void sendMail(String mail) {
        String code = createCode();
        log.info(code);

        MimeMessage message = createMail(mail, code);

        javaMailSender.send(message);
        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisService.setValues(AUTH_CODE_PREFIX + mail,
               code , Duration.ofMillis(this.authCodeExpirationMillis));
    }

    /**
     * 이메일 인증 코드 검증
     */
    @Override
    public void verifyCode(String email, String authCode) throws EMailHandler{
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);

        if (redisAuthCode == null || !redisAuthCode.equals(authCode)) {
            throw new EMailHandler(ErrorStatus.UNAUTHORIZED_EMAIL);
        }
    }
}
