package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.entity.Inquiry;
import com.treasuredigger.devel.service.InquiryService;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer-service/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;
    private final MemberService memberService;

    @GetMapping
    public String listInquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "false") boolean myInquiries,
            @RequestParam(required = false, defaultValue = "false") boolean unansweredOnly,
            Authentication authentication,
            Model model) {
        Page<Inquiry> inquiryPage;
        if (unansweredOnly && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            // 관리자 미답변 문의만 보기
            inquiryPage = inquiryService.getUnansweredInquiriesWithPagination(page, 10);
        } else if (myInquiries && authentication != null) {
            // 일반 사용자의 문의만 보기
            Member member = memberService.findMemberByMid(authentication.getName());
            inquiryPage = inquiryService.getInquiriesByMemberWithPagination(member, page, 10);
        } else {
            // 전체 문의 보기
            inquiryPage = inquiryService.getInquiriesWithPagination(page, 10);
        }
        model.addAttribute("inquiries", inquiryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", inquiryPage.getTotalPages());
        model.addAttribute("hasPrevious", page > 0);
        model.addAttribute("myInquiries", myInquiries);
        model.addAttribute("unansweredOnly", unansweredOnly);
        return "customerService/inquiry/inquiryList"; // inquiryList.html로 이동
    }

    @GetMapping("/register")
    public String registerInquiry(Model model) {
        model.addAttribute("inquiry", new Inquiry());
        return "customerService/inquiry/inquiryRegister"; // inquiryRegister.html로 이동
    }

    @PostMapping
    public String createInquiry(@Valid @ModelAttribute Inquiry inquiry, Authentication authentication) {
        Member member = memberService.findMemberByMid(authentication.getName());
        inquiry.setMember(member);
        Inquiry savedInquiry = inquiryService.saveInquiry(inquiry);
        return "redirect:/customer-service/inquiries";
    }

    @GetMapping("/{id}/modify")
    public String inquiryModify(@PathVariable Long id, Model model) {
        Inquiry inquiry = inquiryService.findInquiryById(id);
        model.addAttribute("inquiry", inquiry);
        return "customerService/inquiry/inquiryModify"; // inquiryModify.html로 이동
    }

    @PostMapping("/{id}")
    public String updateInquiry(@PathVariable Long id, @Valid @ModelAttribute Inquiry inquiry) {
        inquiryService.updateInquiry(id, inquiry);
        return "redirect:/customer-service/inquiries"; // 목록으로 리다이렉트
    }

    @PostMapping("/{id}/delete")
    public String deleteInquiry(@PathVariable Long id) {
        inquiryService.deleteInquiry(id);
        return "redirect:/customer-service/inquiries"; // 목록으로 리다이렉트
    }

    @GetMapping("/{id}")
    public String inquiryDetail(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        Inquiry inquiry = inquiryService.findInquiryById(id);
        Member member = memberService.findMemberByMid(authentication.getName());

        // 작성자가 아니거나 admin이 아닐 경우 에러 메시지 추가
        if (!inquiry.getMember().equals(member) &&
                authentication.getAuthorities().stream()
                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            redirectAttributes.addFlashAttribute("errorMessage", "본인만 열람 가능합니다.");
            return "redirect:/customer-service/inquiries"; // 목록 페이지로 리다이렉트
        }

        model.addAttribute("inquiry", inquiry);
        return "customerService/inquiry/inquiryDetail"; // inquiryDetail.html로 이동
    }

    @PostMapping("/{id}/respond")
    @Secured("ROLE_ADMIN") // 관리자만 접근 가능
    public String respondToInquiry(@PathVariable Long id, @RequestParam String responseContent) {
        Inquiry inquiry = inquiryService.findInquiryById(id);
        inquiry.setResponse(responseContent); // 답변 내용 설정
        inquiryService.updateInquiry(id, inquiry); // 업데이트
        return "redirect:/customer-service/inquiries"; // 목록으로 리다이렉트
    }



}
