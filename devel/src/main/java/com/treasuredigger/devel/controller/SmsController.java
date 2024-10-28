package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.MemberFormDto;
import com.treasuredigger.devel.service.MemberService;
import com.treasuredigger.devel.service.SmsService;
import jakarta.servlet.http.HttpSession;
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
    private final MemberService memberService;

    public SmsController(@Autowired SmsService smsService, @Autowired MemberService memberService){
        this.smsService = smsService;
        this.memberService = memberService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendSms(@RequestBody MemberFormDto memberFormDto) {
        smsService.sendSms(memberFormDto);
        return ResponseEntity.ok("문자를 전송했습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifySms(@RequestBody MemberFormDto memberFormDto, HttpSession session) {
        boolean isVerified = smsService.verifySms(memberFormDto.getPhone(), memberFormDto.getVerificationCode());
        if (isVerified) {
            memberService.verifyPhone(memberFormDto.getMid());
            session.setAttribute("smsVerified", true);

            checkAndUpdateRole(memberFormDto.getMid(), session);
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }
    }

    private void checkAndUpdateRole(String mid, HttpSession session) {
        Boolean emailVerified = (Boolean) session.getAttribute("emailVerified");
        Boolean smsVerified = (Boolean) session.getAttribute("smsVerified");

        if (Boolean.TRUE.equals(emailVerified) && Boolean.TRUE.equals(smsVerified)) {
            memberService.updateMemberRole(mid); // 두 인증이 완료되면 역할 변경
            // 인증 상태 초기화
            session.removeAttribute("emailVerified");
            session.removeAttribute("smsVerified");
        }
    }
}