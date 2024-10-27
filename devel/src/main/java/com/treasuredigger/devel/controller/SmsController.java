package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.SmsVerificationDto;
import com.treasuredigger.devel.service.SmsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    public SmsController(@Autowired SmsService smsService){
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendSms(@RequestBody @Valid SmsVerificationDto smsVerificationDto) {
        smsService.sendSms(smsVerificationDto);
        return ResponseEntity.ok("문자를 전송했습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifySms(@RequestBody @Valid SmsVerificationDto smsVerificationDto) {
        boolean isVerified = smsService.verifySms(smsVerificationDto.getPhone(), smsVerificationDto.getVerificationCode());
        if (isVerified) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }
    }
}