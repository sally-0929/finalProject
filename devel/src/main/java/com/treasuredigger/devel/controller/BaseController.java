package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class BaseController {

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void addCartTotalCountToModel(Model model, Principal principal) {
        int totalCount = 0;
        if (principal != null) {
            String email = principal.getName();
            totalCount = cartService.getTotalCount(email);
            System.out.println("Total count for user " + email + ": " + totalCount);
        }
        model.addAttribute("totalCount", totalCount);
    }
}