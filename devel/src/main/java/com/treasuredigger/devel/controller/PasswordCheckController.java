package com.treasuredigger.devel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members")
public class PasswordCheckController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping(value = "/passwordCheckPage")
    public String passwordCheckPage() {
        return "member/checkPassword"; // 비밀번호 확인 뷰를 반환
    }

    @PostMapping("/checkPassword")
    public ResponseEntity<PasswordCheckResponse> checkPassword(@RequestBody PasswordCheckRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok(new PasswordCheckResponse(true));
        } catch (Exception e) {
            return ResponseEntity.ok(new PasswordCheckResponse(false)); // 비밀번호 불일치 시 응답
        }
    }

    public static class PasswordCheckRequest {
        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class PasswordCheckResponse {
        private boolean valid;

        public PasswordCheckResponse(boolean valid) {
            this.valid = valid;
        }

        public boolean isValid() {
            return valid;
        }
    }
}

