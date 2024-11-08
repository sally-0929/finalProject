package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.constant.MemberGradeStatus;
import com.treasuredigger.devel.dto.BidItemDto;
import com.treasuredigger.devel.dto.MemberFormDto;
import com.treasuredigger.devel.dto.MemberGradeDto;
import com.treasuredigger.devel.dto.WishlistDto;
import com.treasuredigger.devel.entity.Inquiry;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.entity.Wishlist;
import com.treasuredigger.devel.service.DeleteService;
import com.treasuredigger.devel.service.MemberGradeService;
import com.treasuredigger.devel.service.MemberService;
import com.treasuredigger.devel.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.treasuredigger.devel.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberGradeService memberGradeService;
    private final PasswordEncoder passwordEncoder;

    private final DeleteService deleteService;
    private final WishlistService wishlistService;

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

        return "redirect:/members/login";
    }

    @GetMapping(value = "/login")
    public String loginMember(Model model, HttpServletRequest request) {
        // 세션에서 에러 메시지를 가져와서 모델에 추가
        if (request.getSession().getAttribute("loginErrorMsg") != null) {
            model.addAttribute("loginErrorMsg", request.getSession().getAttribute("loginErrorMsg"));
            request.getSession().removeAttribute("loginErrorMsg"); // 메시지를 한 번만 보여주기 위해 제거
        }
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

        int amountNeeded = memberGradeService.calculateAmountForNextGrade(memberGradeDto.getMemberGradeStatus(), memberGradeDto.getMgdesc());
        model.addAttribute("amountNeeded", amountNeeded);

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

        if (model.containsAttribute("message")) {
            model.addAttribute("message", model.asMap().get("message"));
        }

        return "member/memberUpdate";
    }

    @PostMapping(value = "/{mid}")
    public String updateMember(@PathVariable String mid, @Valid @ModelAttribute Member member, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        memberService.updateMember(mid, member);
        redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        return "redirect:/members/login";
    }

    @GetMapping(value = "/memberDelete")
    public String memberDeleteForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mid = authentication.getName(); // 현재 로그인한 사용자의 아이디

        // 회원 정보를 가져오기
        Member member = memberService.findMemberByMid(mid);
        model.addAttribute("member", member);
        return "member/memberDeleteConfirm";
    }

    @PostMapping("/memberDeleteConfirm")
    public String deleteMember(@RequestParam("mid") String mid, HttpServletRequest request, HttpServletResponse response)
    { // 회원 탈퇴
        boolean isDelete =  deleteService.deleteItem(mid); //item 삭제
        System.out.println("delete ?" + isDelete);

       memberService.deleteMember(mid);
        // 로그아웃 처리
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }

    @GetMapping(value = "/passwordCheck")
    public String passwordCheck() {
        return "member/passwordCheck"; // 비밀번호 확인 뷰
    }

    @PostMapping(value = "/passwordCheck")
    public ResponseEntity<String> verifyPassword(@RequestParam String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mid = authentication.getName(); // 현재 로그인한 사용자의 아이디
        Member member = memberService.findMemberByMid(mid);

        // 비밀번호 확인
        if (passwordEncoder.matches(password, member.getPassword())) {
            return ResponseEntity.ok("비밀번호가 확인되었습니다."); // 성공 메시지
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀립니다."); // 실패 메시지
        }
    }

    @GetMapping("/wishlist")
    public String wishlist(Principal principal, Model model){

        String email = principal.getName();
        List<WishlistDto> wishlists  = wishlistService.getWishlistByMember(email);
        System.out.println("wishif test"  + wishlists);
        model.addAttribute("wishlist" , wishlists);

        return "member/wishlist";

    }

}