package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.service.EmailService;
import com.treasuredigger.devel.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private MemberService memberService;

    @PostMapping("/check")
    public ResponseEntity<String> checkEmail(@RequestParam String email) {
        boolean emailExists = memberService.findMemberByEmail(email) != null;

        if (emailExists) {
            return ResponseEntity.ok("un-useable");
        }
        return ResponseEntity.ok("useable");
    }

    @PostMapping("/sendAuth")
    public ResponseEntity<String> sendEmailAuth(@RequestParam String email, HttpSession session) {
        String codeAuth = generateAuthCode(); // 인증 코드 생성
        emailService.sendEmail(email, "이메일 인증 코드", "인증 코드: " + codeAuth);

        session.setAttribute("codeAuth", codeAuth);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/successAuth")
    public ResponseEntity<String> verifyAuthCode(@RequestParam String code, HttpSession session) {
        String storedCode = (String) session.getAttribute("codeAuth");

        if (storedCode != null && storedCode.equals(code)) {
            session.removeAttribute("codeAuth"); // 인증 후 세션에서 코드 제거
            return ResponseEntity.ok("인증 성공");
        }

        return ResponseEntity.badRequest().body("인증 코드가 유효하지 않거나 만료되었습니다.");
    }

    private String generateAuthCode() {
        // 간단한 랜덤 코드 생성 로직
        return String.valueOf((int) (Math.random() * 999999));
    }
}