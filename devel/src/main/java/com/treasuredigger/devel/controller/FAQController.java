package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.entity.FAQ;
import com.treasuredigger.devel.service.FAQService;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer-service/faqs")
public class FAQController {

    private final FAQService faqService;

    @GetMapping
    public String listFAQs(Model model) {
        model.addAttribute("faqs", faqService.getAllFAQs());
        return "customerService/faq/faqList"; // faqList.html로 이동
    }

    @GetMapping("/register")
    @Secured("ROLE_ADMIN")
    public String registerFAQ(Model model) {
        model.addAttribute("faq", new FAQ());
        return "customerService/faq/faqRegister"; // faqRegister.html로 이동
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public String createFAQ(@Valid @ModelAttribute FAQ faq) {
        faqService.saveFAQ(faq);
        return "redirect:/customer-service/faqs";
    }

    @GetMapping("/{id}/modify")
    @Secured("ROLE_ADMIN")
    public String modifyFAQ(@PathVariable Long id, Model model) {
        FAQ faq = faqService.findFAQById(id);
        model.addAttribute("faq", faq);
        return "customerService/faq/faqModify"; // faqModify.html로 이동
    }

    @PostMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public String updateFAQ(@PathVariable Long id, @Valid @ModelAttribute FAQ faq) {
        faqService.updateFAQ(id, faq);
        return "redirect:/customer-service/faqs";
    }

    @PostMapping("/{id}/delete")
    @Secured("ROLE_ADMIN")
    public String deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return "redirect:/customer-service/faqs";
    }
}
