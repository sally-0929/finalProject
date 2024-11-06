package com.treasuredigger.devel.controller;


import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.treasuredigger.devel.dto.PaymentDto;
import com.treasuredigger.devel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/validation/{imp_uid}")
    public IamportResponse<Payment> validateIamport(@PathVariable String imp_uid) throws IamportResponseException, IOException {
        log.info("imp_uid: {}", imp_uid);
        log.info("validateIamport");
        return paymentService.validateIamport(imp_uid);
    }

    @PostMapping("/order")
    public ResponseEntity<String> processOrder(@RequestBody PaymentDto paymentDto) {
        // 주문 정보를 로그에 출력
        log.info("Received orders: {}", paymentDto.toString());
        // 성공적으로 받아들였다는 응답 반환
        return ResponseEntity.ok(paymentService.saveOrder(paymentDto));
    }

    @PostMapping("/cancel/{imp_uid}")
    public IamportResponse<Payment> cancelPayment(@PathVariable String imp_uid) throws IamportResponseException, IOException {
        return paymentService.cancelPayment(imp_uid);
    }

    @GetMapping("/paymentP")
    public String showPaymentPage() {
        System.out.println("여기 왜 안옴");
        return "payment/paymentP";
    }
}

