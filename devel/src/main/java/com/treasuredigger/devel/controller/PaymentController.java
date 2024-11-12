package com.treasuredigger.devel.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.treasuredigger.devel.config.IamportConfig;
import com.treasuredigger.devel.constant.OrderStatus;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.Order;
import com.treasuredigger.devel.entity.OrderItem;  // OrderItem 추가
import com.treasuredigger.devel.repository.OrderRepository;
import com.treasuredigger.devel.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final BidService bidService;
    private final OrderService orderService;
    private final MemberService memberService;
    private final BidItemService bidItemService;
    private final RefundService refundService;
    private final IamportConfig iamportConfig;

    /**
     * 아임포트 결제 검증
     */
    @RequestMapping(value = "/validation/{orderId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> validateIamport(@PathVariable Long orderId, @RequestParam("imp_uid") String imp_uid, Model model, Principal principal) throws IamportResponseException, IOException {

        // 결제 검증 결과
        IamportResponse<Payment> paymentResponse = paymentService.validateIamport(imp_uid);

// paymentResponse가 null이 아니고, 응답 코드가 0이면 결제 검증 성공
        if (paymentResponse != null && paymentResponse.getCode() == 0) {
            // response.getResponse()를 사용해서 Payment 객체 확인
            Payment payment = paymentResponse.getResponse();

            // 결제 상태 확인
            if (payment != null && "paid".equals(payment.getStatus())) {
                // 결제 성공 처리
                log.info("결제 성공 - 주문 번호: {}, 상태: {}, 금액: {}", payment.getMerchantUid(), payment.getStatus(), payment.getAmount());
                String merchantUid = payment.getMerchantUid();
//                String orderIdString = orderId1.substring(6); // "order_" 이후의 부분을 가져옴
//
//                Long orderId = Long.parseLong(orderIdString); // 숫자 부분을 Long으로 변환
                orderService.changeOrderStatus(orderId, OrderStatus.PAYMENT_COMPLETED);

                try {
                    BidItem bidItem = orderService.getBidItemByOrderId(orderId);
                    String bidItemId = bidItem.getBidItemId();
                    long bidNowPrice = bidItem.getMaxPrice();
                    Member member = memberService.findMemberByMid(principal.getName());
                    Long mid = member.getId();

                    bidService.saveBid(bidItemId, mid, bidNowPrice, "Y");
                    bidItemService.updateItemStatuses();
                    return new ResponseEntity<>("success", HttpStatus.OK);  // 결제 성공 템플릿
                } catch (Exception e) {
                    return new ResponseEntity<>("success", HttpStatus.OK);  // 결제 성공 템플릿
                }

            } else {
                // 결제 실패 처리
                log.info("결제 실패 - 주문 번호: {}, 상태: {}", payment.getMerchantUid(), payment.getStatus());
//                model.addAttribute("errorMessage", "결제 실패 또는 결제 정보가 유효하지 않습니다.");
                return new ResponseEntity<>("fail", HttpStatus.OK);  // 결제 실패 템플릿
            }
        } else {
            // 결제 검증 실패
            log.error("결제 검증 실패: 응답 코드 {}, 응답 메시지 {}", paymentResponse.getCode(), paymentResponse.getMessage());
            model.addAttribute("errorMessage", "결제 검증 실패");
            return new ResponseEntity<>("invalid", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{orderId}/refund", method = RequestMethod.POST)
    public ResponseEntity<String> refundOrder(@PathVariable Long orderId, @RequestParam String reason, @RequestParam String merchantUid) {
        try {
            // 주문 조회
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

            log.info("Refund request received: orderId={}, reason={}", orderId, reason);

            // Iamport API와의 통합을 위해 액세스 토큰을 받아옴
            String accessToken = refundService.getToken(iamportConfig.getApiKey(), iamportConfig.getApiSecret());

            // 환불 요청
            refundService.refundRequest(accessToken, merchantUid, reason);

            log.info("환불 요청 성공 - orderId: {}, merchantUid: {}, reason: {}", orderId, merchantUid, reason);

            return ResponseEntity.ok("환불 요청이 완료되었습니다.");
        } catch (Exception e) {
            log.error("환불 요청 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("환불 요청 처리에 실패했습니다.");
        }
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
                    .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

            // 결제 처리 (예: 123 금액으로 결제 처리)
            paymentService.processOrderPayment(orderId);

            // 모델에 주문 정보 추가
            model.addAttribute("order", order);

            // 주문 확인 페이지로 이동
            return "payment/paymentSuccess";  // 결제 성공 페이지
        } catch (Exception e) {
            log.error("주문 확인 중 에러 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            return "error";  // 에러 페이지
        }
    }
}
