package com.treasuredigger.devel.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.treasuredigger.devel.constant.PaymentStatus;
import com.treasuredigger.devel.dto.PaymentDto;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.Order;
import com.treasuredigger.devel.entity.PaymentEntity;
import com.treasuredigger.devel.repository.ItemRepository;
import com.treasuredigger.devel.repository.MemberRepository;
import com.treasuredigger.devel.repository.OrderRepository;
import com.treasuredigger.devel.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final IamportClient iamportClient;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;  // 상품 조회용 repository
    private final MemberRepository memberRepository;  // 회원 조회용 repository

    // 아임포트 결제 확인
    public IamportResponse<Payment> validateIamport(String imp_uid) {
        try {
            IamportResponse<Payment> payment = iamportClient.paymentByImpUid(imp_uid);
            Payment paymentResponse = payment.getResponse();
            log.info("결제 요청 응답. 결제 내역 - 주문 번호: {}, 상태: {}, 금액: {}",
                    paymentResponse.getImpUid(), paymentResponse.getStatus(), paymentResponse.getAmount());
            return payment;
        } catch (Exception e) {
            log.error("결제 확인 중 에러 발생: {}", e.getMessage());
            return null;
        }
    }

    // 아임포트 결제 취소
    public IamportResponse<Payment> cancelPayment(String imp_uid) {
        try {
            CancelData cancelData = new CancelData(imp_uid, true);
            IamportResponse<Payment> payment = iamportClient.cancelPaymentByImpUid(cancelData);
            return payment;
        } catch (Exception e) {
            log.error("결제 취소 중 에러 발생: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public String saveOrder(PaymentDto paymentDto) {
        try {
            // 주문 정보 및 결제 관련 정보를 처리
            log.info("PaymentDto 내용 확인: {}", paymentDto);

            // Order 조회
            Order order = orderRepository.findById(paymentDto.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));

            // 결제 정보 설정
            PaymentEntity payment = paymentDto.toEntity();
            payment.setOrder(order);  // 주문 정보 연결
            payment.setStatus(PaymentStatus.PAID);  // 결제 상태 설정 (성공)
            payment.setPaidAt(LocalDateTime.now());  // 결제 완료 시간

            // 결제 저장
            paymentRepository.save(payment);

            log.info("결제 정보 저장 완료. 결제 ID: {}", payment.getId());

            return "주문 정보가 성공적으로 저장되었습니다.";
        } catch (Exception e) {
            log.error("주문 저장 중 에러 발생: {}", e.getMessage(), e);
            cancelPayment(paymentDto.getImpUid());  // 결제 취소 처리
            return "주문 정보 저장에 실패했습니다.";
        }
    }
}


