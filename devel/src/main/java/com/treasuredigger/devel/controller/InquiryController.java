package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.entity.Inquiry;
import com.treasuredigger.devel.service.InquiryService;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;
    private final MemberService memberService;

    @GetMapping
    public String listInquiries(Model model) {
        model.addAttribute("inquiries", inquiryService.getAllInquiries());
        return "inquiry/inquiryList"; // inquiryList.html로 이동
    }

    @GetMapping("/register")
    public String registerInquiry(Model model) {
        model.addAttribute("inquiry", new Inquiry());
        return "inquiry/inquiryRegister"; // inquiryRegister.html로 이동
    }

    @PostMapping
    public String createInquiry(@Valid @ModelAttribute Inquiry inquiry, Authentication authentication) {
        Member member = memberService.findMemberByMid(authentication.getName());
        inquiry.setMember(member);
        Inquiry savedInquiry = inquiryService.saveInquiry(inquiry);
        return "redirect:/inquiries";
    }

    @GetMapping("/{id}/modify")
    public String inquiryModify(@PathVariable Long id, Model model) {
        Inquiry inquiry = inquiryService.findInquiryById(id);
        model.addAttribute("inquiry", inquiry);
        return "inquiry/inquiryModify"; // inquiryModify.html로 이동
    }

    @PostMapping("/{id}")
    public String updateInquiry(@PathVariable Long id, @Valid @ModelAttribute Inquiry inquiry) {
        inquiryService.updateInquiry(id, inquiry);
        return "redirect:/inquiries"; // 목록으로 리다이렉트
    }

    @PostMapping("/{id}/delete")
    public String deleteInquiry(@PathVariable Long id) {
        inquiryService.deleteInquiry(id);
        return "redirect:/inquiries"; // 목록으로 리다이렉트
    }
}
