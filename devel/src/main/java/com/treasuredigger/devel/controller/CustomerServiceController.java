package com.treasuredigger.devel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer-service")
public class CustomerServiceController {

    @GetMapping
    public String customerService() {
        return "customerService/customerService"; // customerService.html로 이동
    }
}
