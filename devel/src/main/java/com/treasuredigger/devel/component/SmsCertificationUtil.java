package com.treasuredigger.devel.component;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SmsCertificationUtil {

    @Value("${sms.api.key}") // coolsms의 API 키 주입
    private String apiKey;

    @Value("${sms.api.secret}") // coolsms의 API 비밀키 주입
    private String apiSecret;

    @Value("${sms.api.domain}")
    private String doMain;

    @Value("${sms.api.number}") // 발신자 번호 주입
    private String fromNumber;

    DefaultMessageService messageService; // 메시지 서비스를 위한 객체

    @PostConstruct // 의존성 주입이 완료된 후 초기화를 수행하는 메서드
    public void init(){
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, doMain); // 메시지 서비스 초기화
    }

    // 단일 메시지 발송
    public void sendSMS(String to, String certificationCode) {
        try {
            log.info("Sending SMS to: {}", to); // 호출 확인
            Message message = new Message();
            message.setFrom(fromNumber);
            message.setTo(to);
            message.setText("본인확인 인증번호는 " + certificationCode + "입니다.");

            this.messageService.sendOne(new SingleMessageSendingRequest(message));
            log.info("SMS sent successfully.");
        } catch (Exception e) {
            log.error("Error sending SMS", e);
        }
    }
}
