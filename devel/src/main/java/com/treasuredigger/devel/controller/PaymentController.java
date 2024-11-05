package com.treasuredigger.devel.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.Order;
import com.treasuredigger.devel.service.IamportService;
import com.treasuredigger.devel.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/v1")
public class PaymentController {

    @Autowired
    HttpSession session;

    @Autowired
    OrderService orderService;

    private final IamportService iamportService;
    private IamportClient iamportClient;

    @Autowired
    public PaymentController(IamportService iamportService, IamportClient iamportClient) {
        this.iamportService = iamportService;
        this.iamportClient = iamportClient;
    }

    // 결제 요청을 처리하는 메서드
    @PostMapping("/payment")
    @ResponseBody
    public Map<String, Object> processPayment(
            @RequestParam("imp_uid") String imp_uid,
            @RequestParam("order_idx") int order_idx,
            @RequestParam("order_amount") int order_amount) {

        Map<String, Object> resultMap = new HashMap<>();

        Member user = (Member) session.getAttribute("user");

        if (user == null) {
            resultMap.put("result", "fail_no_user");
            return resultMap;
        }

        try {
            IamportResponse<Payment> paymentResponse = iamportService.getPaymentInfo(imp_uid);
            Payment payment = paymentResponse.getResponse();

            // 결제 금액 확인
            if (payment.getAmount().compareTo(new BigDecimal(order_amount)) == 0) {
                // 결제 처리 후 필요한 추가 작업 (예: DB에 결제 정보 저장)
                resultMap.put("result", "success");
            } else {
                resultMap.put("result", "fail_not_same_payment");
            }

        } catch (IamportResponseException | IOException e) {
            resultMap.put("result", "fail_exception");
        }

        return resultMap;
    }

    // 결제 완료 후 이동할 화면
    @RequestMapping("/success")
    public String paymentSuccess(
            @RequestParam("performance_idx") int performance_idx,
            @RequestParam("order_idx") int order_idx,
            @RequestParam("used_point2") String used_point2,
            String email,
            Pageable pageable,
            Model model) {

        Member user = (Member) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        // 주문 정보 가져오기 (DB에서 조회)
        Order order = (Order) orderService.getOrderList(email, pageable);
        model.addAttribute("order", order);
        model.addAttribute("used_point2", used_point2);

        return "payment/success";
    }
}


