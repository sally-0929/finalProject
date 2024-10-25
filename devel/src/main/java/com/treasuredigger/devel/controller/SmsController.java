package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.MemberFormDto;
import com.treasuredigger.devel.service.SmsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> SendSMS(@RequestBody @Valid MemberFormDto memberFormDto){
        smsService.sendSms(memberFormDto);
        return ResponseEntity.ok("문자를 전송했습니다.");
    }
}
