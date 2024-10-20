package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.BidItemFormDto;
import com.treasuredigger.devel.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bid")
@Log4j2
public class BidController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public void bidlist(){
    }

    @GetMapping("/register")
    public String bidregister(Model model) {
        model.addAttribute("bidItemFormDto", new BidItemFormDto());
        model.addAttribute("categories", categoryService.list());
        log.info("category" + categoryService.list());

        return "bid/register";
    }
}
