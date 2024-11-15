package com.treasuredigger.devel.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.treasuredigger.devel.config.IamportConfig;
import com.treasuredigger.devel.constant.MemberGradeStatus;
import com.treasuredigger.devel.constant.OrderStatus;
import com.treasuredigger.devel.constant.PaymentStatus;
import com.treasuredigger.devel.entity.*;
import com.treasuredigger.devel.repository.OrderRepository;
import com.treasuredigger.devel.repository.PaymentRepository;
import com.treasuredigger.devel.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final BidService bidService;
    private final OrderService orderService;
    private final MemberService memberService;
    private final BidItemService bidItemService;
    private final RefundService refundService;
    private final IamportConfig iamportConfig;
    private final PointService pointService;

    /**
     * 아임포트 결제 검증
     */
    @RequestMapping(value = "/validation/{orderId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> validateIamport(@PathVariable Long orderId,
                                                  @RequestParam("imp_uid") String imp_uid,
                                                  @RequestParam(value = "usePoints", required = false) BigDecimal usePoints,
                                                  Model model,
                                                  Principal principal) throws IamportResponseException, IOException {

        // 결제 검증 결과
        IamportResponse<Payment> paymentResponse = paymentService.validateIamport(imp_uid);

        // paymentResponse가 null이 아니고, 응답 코드가 0이면 결제 검증 성공
        if (paymentResponse != null && paymentResponse.getCode() == 0) {
            // response.getResponse()를 사용해서 Payment 객체 확인
            Payment payment = paymentResponse.getResponse();
            BigDecimal finalAmount = payment.getAmount(); // 결제 금액을 BigDecimal로 저장

            // 결제 상태 확인
            if (payment != null && "paid".equals(payment.getStatus())) {
                // 결제 성공 처리
                log.info("결제 성공 - 주문 번호: {}, 상태: {}, 금액: {}", payment.getMerchantUid(), payment.getStatus(), payment.getAmount());
                orderService.changeOrderStatus(orderId, OrderStatus.PAYMENT_COMPLETED);

                try {
                    // 2. 회원 및 포인트 처리 (포인트 차감 먼저 처리)
                    Member member = memberService.findMemberByMid(principal.getName());
                    Long mid = member.getId();
                    BigDecimal availablePoints = BigDecimal.valueOf(member.getPoints());  // int -> BigDecimal 변환

                    log.info("회원 ID: {}, 보유 포인트: {}", mid, availablePoints);

                    // 포인트 사용 여부 체크 (포인트 사용 요청이 있을 경우)
                    if (usePoints != null && usePoints.compareTo(BigDecimal.ZERO) > 0) {
                        // 포인트 사용 가능 여부 체크
                        if (usePoints.compareTo(availablePoints) > 0) {
                            log.warn("사용하려는 포인트가 보유한 포인트보다 큽니다. 사용하지 않습니다.");
                            return new ResponseEntity<>("fail", HttpStatus.OK);  // 포인트 부족
                        }

                        member.deductPoints(usePoints.intValue());  // 포인트 차감

                        log.info("포인트 사용: {}원, 결제 금액 차감 후 금액: {}", usePoints, finalAmount);
                    }

                    Order order = orderService.getOrderById(orderId);

                    // 1. 결제 정보 DB에 저장 (포인트 차감 후 금액 저장)
                    PaymentEntity paymentEntity = new PaymentEntity();
                    paymentEntity.setMerchantUid(payment.getMerchantUid());
                    paymentEntity.setAmount(finalAmount.intValue());  // 결제 금액 저장 (최종 결제 금액)
                    paymentEntity.setPaymentDate(LocalDateTime.now());
                    paymentEntity.setOrder(order);
                    String status = payment.getStatus();
                    if (status != null) {
                        // 예시: Enum 타입으로 변환하는 경우
                        try {
                            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());  // enum 변환
                            paymentEntity.setPaymentStatus(paymentStatus);  // 변환된 enum을 설정
                        } catch (IllegalArgumentException e) {
                            // 변환할 수 없는 상태 값 처리
                            log.error("유효하지 않은 결제 상태: {}", status);
                        }
                    }
                    paymentRepository.save(paymentEntity);  // 결제 정보 DB 저장

                    // 3. 포인트 적립
                    MemberGradeStatus memberGrade = member.getMemberGrade().getMemberGradeStatus();
                    int pointRate = memberGrade.getPointRate();  // 회원 등급에 따른 포인트 비율
                    log.info("회원 ID: {}, 등급: {}, 포인트 비율: {}%", mid, memberGrade.name(), pointRate);

                    // 결제 금액을 포인트로 환산하여 적립
                    BigDecimal pointsToAdd = finalAmount.multiply(BigDecimal.valueOf(pointRate))
                            .divide(BigDecimal.valueOf(100));  // 포인트 계산

                    log.info("결제 금액: {}원 -> 포인트 적립: {}원 (포인트 비율: {}%)", finalAmount, pointsToAdd, pointRate);

                    // 포인트 적립
                    pointService.addPoints(mid, pointsToAdd);
                    log.info("회원 {}에게 {} 포인트 적립 완료", mid, pointsToAdd);

                    // 경매 처리 부분: 경매 상품이면 BidItem 처리, 일반 상품은 처리하지 않음
                    for (OrderItem orderItem : order.getOrderItems()) {
                        if (orderItem.isBidItem()) {
                            BidItem bidItem = orderItem.getBiditem();
                            String bidItemId = bidItem.getBidItemId();
                            long bidNowPrice = bidItem.getMaxPrice();
                            bidService.saveBid(bidItemId, mid, bidNowPrice, "Y");
                            bidItemService.updateItemStatuses();
                        }
                    }

                    return new ResponseEntity<>("success", HttpStatus.OK);  // 결제 성공 템플릿
                } catch (Exception e) {
                    log.error("결제 처리 중 예외 발생", e);
                    return new ResponseEntity<>("fail", HttpStatus.OK);  // 예외 발생 시 실패 처리
                }
            } else {
                // 결제 실패 처리
                log.info("결제 실패 - 주문 번호: {}, 상태: {}", payment.getMerchantUid(), payment.getStatus());
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

            orderService.cancelOrder(orderId);

            // PaymentEntity 상태를 CANCELED로 변경
            PaymentEntity paymentEntity = paymentService.findPaymentByMerchantUid(merchantUid); // merchantUid로 PaymentEntity 조회
            paymentEntity.cancelPayment();  // 결제 상태를 CANCELED로 변경
            paymentService.save(paymentEntity);  // 변경된 PaymentEntity 저장

            // 환불 사유 저장
            refundService.saveRefundReason(merchantUid, reason);

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
            model.addAttribute("member", order.getMember());

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

            // 해당 주문에 대한 PaymentEntity 조회
            PaymentEntity paymentEntity = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("결제 정보가 없습니다."));

            // 모델에 주문 정보 추가
            model.addAttribute("order", order);
            model.addAttribute("payment", paymentEntity);

            // 주문 확인 페이지로 이동
            return "payment/paymentSuccess";  // 결제 성공 페이지
        } catch (Exception e) {
            log.error("주문 확인 중 에러 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            return "error";  // 에러 페이지
        }
    }
}
