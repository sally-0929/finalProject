package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.constant.MemberGradeStatus;
import com.treasuredigger.devel.dto.MemberFormDto;
import com.treasuredigger.devel.dto.MemberGradeDto;
import com.treasuredigger.devel.service.MemberGradeService;
import com.treasuredigger.devel.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.treasuredigger.devel.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberGradeService memberGradeService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "/member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }

    @GetMapping(value = "/myPage")
    public String myPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mid = authentication.getName(); // 현재 로그인한 사용자의 아이디

        // 회원 정보를 가져오기
        Member member = memberService.findMemberByMid(mid);
        model.addAttribute("member", member);

        // 회원 등급 정보를 가져오기
        MemberGradeDto memberGradeDto = memberGradeService.getMemberGrade(member.getId());
        model.addAttribute("memberGradeStatus", memberGradeDto.getMemberGradeStatus());

        return "member/myPage";
    }

    @GetMapping(value = "/memberUpdate")
    public String memberUpdate(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mid = authentication.getName(); // 현재 로그인한 사용자의 아이디

        // 회원 정보를 가져오기
        Member member = memberService.findMemberByMid(mid);
        model.addAttribute("member", member);

        // 회원 등급을 가져오기
        MemberGradeDto memberGradeDto = memberGradeService.getMemberGrade(member.getId());
        model.addAttribute("memberGradeStatus", memberGradeDto.getMemberGradeStatus().toString()); // 등급 상태를 문자열로 추가

        System.out.println("회원: {}" + member);
        System.out.println("회원 등급: {}" + memberGradeDto.getMemberGradeStatus());

        return "member/memberUpdate";
    }

}