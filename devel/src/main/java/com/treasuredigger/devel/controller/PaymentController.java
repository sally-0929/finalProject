package com.treasuredigger.devel.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.treasuredigger.devel.dto.PaymentDto;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.Order;
import com.treasuredigger.devel.entity.OrderItem;  // OrderItem 추가
import com.treasuredigger.devel.repository.ItemRepository;
import com.treasuredigger.devel.repository.MemberRepository;
import com.treasuredigger.devel.repository.OrderRepository;
import com.treasuredigger.devel.service.BidService;
import com.treasuredigger.devel.service.MemberService;
import com.treasuredigger.devel.service.OrderService;
import com.treasuredigger.devel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final BidService bidService;
    private final OrderService orderService;
    private final MemberService memberService;

    /**
     * 아임포트 결제 검증
     */
    @PostMapping("/validation/{imp_uid}")
    public String validateIamport(@PathVariable String imp_uid, Model model, Principal principal) throws IamportResponseException, IOException {
        log.info("imp_uid: {}", imp_uid);

        // 결제 정보 확인
        IamportResponse<Payment> paymentResponse = paymentService.validateIamport(imp_uid);

        // 결제 확인 결과를 모델에 추가
        if (paymentResponse != null && paymentResponse.getCode() == 0) {
            // 결제 성공 시
            BidItem bidItem = orderService.getBidItemByOrderId(902L);
            String bidItemId = bidItem.getBidItemId();
            long bidNowPrice = bidItem.getMaxPrice();
            Member member =  memberService.findMemberByMid(principal.getName());
            Long mid = member.getId();

            bidService.saveBid(bidItemId,mid,bidNowPrice, "Y");
            model.addAttribute("payment", paymentResponse.getResponse());
            return "payment/paymentSuccess";  // 결제 성공 템플릿
        } else {
            // 결제 실패 시
            model.addAttribute("errorMessage", "결제 실패 또는 결제 정보가 유효하지 않습니다.");
            return "payment/paymentFailure";  // 결제 실패 템플릿
        }
    }

    /**
     * 주문 처리
     */
    @PostMapping("/order")
    public ResponseEntity<String> processOrder(@RequestBody PaymentDto paymentDto) {
        log.info("Received order: {}", paymentDto.toString());
        return ResponseEntity.ok(paymentService.saveOrder(paymentDto));  // 주문 저장 후 응답
    }

    /**
     * 결제 취소
     */
    @PostMapping("/cancel/{imp_uid}")
    public IamportResponse<Payment> cancelPayment(@PathVariable String imp_uid) throws IamportResponseException, IOException {
        return paymentService.cancelPayment(imp_uid);
    }

    /**
     * 결제 페이지로 이동
     */
    @GetMapping("/paymentP")
    public String showPaymentPage(@RequestParam Long orderId, Model model) {
        log.info("Received orderId: {}", orderId); // 전달된 orderId 로그 출력

        try {
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            log.info("Found order: {}", orderOptional.isPresent() ? "Yes" : "No");

            // 주문 정보 조회
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다. orderId: " + orderId));
            // 주문에 포함된 상품 목록 조회 (OrderItem을 통해 Item을 조회)
            List<OrderItem> orderItems = order.getOrderItems();

            // 모델에 데이터 추가
            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderItems);

            return "payment/paymentP";  // 결제 페이지 반환
        } catch (Exception e) {
            log.error("결제 페이지 로드 중 에러 발생: {}", e.getMessage(), e);
            // 에러 페이지로 리디렉션하거나 에러 메시지를 전달
            model.addAttribute("errorMessage", e.getMessage());
            return "error"; // 에러 페이지
        }
    }

    @GetMapping("/confirmation")
    public String viewOrderConfirmation(@RequestParam Long orderId, Model model) {
        try {
            // 주문 정보 조회
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다. orderId: " + orderId));

            // 모델에 주문 정보 추가
            model.addAttribute("order", order);

            // 주문 확인 페이지로 이동
            return "payment/paymentSuccess";  // 주문 확인 페이지
        } catch (Exception e) {
            log.error("주문 확인 중 에러 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            return "error";  // 에러 페이지
        }
    }
}
