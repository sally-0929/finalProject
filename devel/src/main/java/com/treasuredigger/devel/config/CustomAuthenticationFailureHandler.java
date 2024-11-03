package com.treasuredigger.devel.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 세션에 로그인 에러 메시지를 저장
        request.getSession().setAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");

        // 로그인 페이지로 리디렉션
        response.sendRedirect("/members/login");
    }

}
