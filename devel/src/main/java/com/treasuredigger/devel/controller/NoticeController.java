package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.entity.Notice;
import com.treasuredigger.devel.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer-service/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public String listNotices(Model model) {
        model.addAttribute("notices", noticeService.getAllNotices());
        return "customerService/notice/noticeList"; // noticeList.html로 이동
    }

    @GetMapping("/register")
    @Secured("ROLE_ADMIN")
    public String registerNotice(Model model) {
        model.addAttribute("notice", new Notice());
        return "customerService/notice/noticeRegister"; // noticeRegister.html로 이동
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public String createNotice(@Valid @ModelAttribute Notice notice) {
        noticeService.saveNotice(notice);
        return "redirect:/customer-service/notices";
    }

    @GetMapping("/{id}/modify")
    @Secured("ROLE_ADMIN")
    public String modifyNotice(@PathVariable Long id, Model model) {
        Notice notice = noticeService.findNoticeById(id);
        model.addAttribute("notice", notice);
        return "customerService/notice/noticeModify"; // noticeModify.html로 이동
    }

    @PostMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public String updateNotice(@PathVariable Long id, @Valid @ModelAttribute Notice notice) {
        noticeService.updateNotice(id, notice);
        return "redirect:/customer-service/notices";
    }

    @PostMapping("/{id}/delete")
    @Secured("ROLE_ADMIN")
    public String deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/customer-service/notices";
    }
}
