package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.service.EmailService;
import com.treasuredigger.devel.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private MemberService memberService;

    @PostMapping("/check")
    public ResponseEntity<String> checkEmail(@RequestParam String email) {
        Optional<Member> memberOpt = memberService.findMemberByEmail(email);

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            if (member.isEmailVerified()) {
                return ResponseEntity.ok("already_verified"); // 이미 인증된 이메일
            }
            return ResponseEntity.ok("un-useable"); // 사용 중인 이메일
        }
        return ResponseEntity.ok("useable"); // 사용 가능한 이메일
    }

    @PostMapping("/sendAuth")
    public ResponseEntity<String> sendEmailAuth(@RequestParam String email, HttpSession session) {
        Member member = memberService.findNormalMemberByEmail(email);
        //        Optional<Member> memberOpt = memberService.findMemberByEmail(email);
//
//        // 이메일이 존재하지 않는 경우 처리
//        if (memberOpt.isEmpty()) {
//            return ResponseEntity.status(404).body("존재하지 않는 이메일입니다.");
//        }
//
//        Member member = memberOpt.get(); // Optional에서 Member 가져오기

        // 이메일이 이미 인증된 경우 처리
        if(member != null && member.isEmailVerified()){
//        if (member.isEmailVerified()) {
            return ResponseEntity.ok("already_verified");
        }

        String codeAuth = generateAuthCode(); // 인증 코드 생성
        emailService.sendEmail(email, "이메일 인증 코드", "인증 코드: " + codeAuth);

        session.setAttribute("codeAuth", codeAuth);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/successAuth")
    public ResponseEntity<String> verifyAuthCode(@RequestParam String code, @RequestParam String mid, HttpSession session) {
        String storedCode = (String) session.getAttribute("codeAuth");

        if (storedCode != null && storedCode.equals(code)) {
            session.removeAttribute("codeAuth"); // 인증 후 세션에서 코드 제거
            memberService.verifyEmail(mid);
            session.setAttribute("emailVerified", true);

            checkAndUpdateRole(mid, session);
            return ResponseEntity.ok("인증 성공");
        }

        return ResponseEntity.badRequest().body("인증 코드가 유효하지 않거나 만료되었습니다.");
    }

    private String generateAuthCode() {
        // 간단한 랜덤 코드 생성 로직
        return String.valueOf((int) (Math.random() * 999999));
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