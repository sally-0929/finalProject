package com.treasuredigger.devel.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.treasuredigger.devel.constant.PaymentStatus;
import com.treasuredigger.devel.entity.Order;
import com.treasuredigger.devel.entity.PaymentEntity;
import com.treasuredigger.devel.repository.OrderRepository;
import com.treasuredigger.devel.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final IamportClient iamportClient;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

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
            log.info("결제 취소 성공: {}", imp_uid);
            return payment;
        } catch (Exception e) {
            log.error("결제 취소 중 에러 발생: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public void processOrderPayment(Long orderId) {
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 총 결제 금액 계산 (Order에서 제공하는 getTotalPrice 메서드를 사용)
        int paymentAmount = order.getTotalPrice();

        // 결제 상태를 "COMPLETED"로 설정
        PaymentStatus paymentStatus = PaymentStatus.PAID;

        // 주문에 결제 추가
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setAmount(paymentAmount);  // 총 결제 금액 설정
        paymentEntity.setPaymentStatus(paymentStatus);  // 결제 상태 설정
        paymentEntity.setOrder(order);  // 주문과 결제 연결
        paymentEntity.setPaymentDateNow();  // 결제 완료 시간 설정

        // 주문에 포함된 merchantUid 설정
        paymentEntity.setMerchantUid(order.getMerchantUid());  // 주문의 merchantUid를 결제 정보에 설정

        // 결제 내역 저장
        log.info("결제 정보 저장 완료. 결제 ID: {}", paymentEntity.getId());

        // 주문에 결제 정보 추가 (주문 객체에 결제 내역 연결)
        order.addPayment(paymentEntity);

        // 주문 저장 (결제 정보가 추가된 상태로 저장)
        orderRepository.save(order);
    }

    public void changePaymentStatus(Long paymentId, PaymentStatus newStatus) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + paymentId));
        payment.setPaymentStatus(newStatus);
        paymentRepository.save(payment);
    }

    // merchantUid로 결제 정보를 찾는 메서드 추가
    public PaymentEntity findPaymentByMerchantUid(String merchantUid) {
        return paymentRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다. merchantUid: " + merchantUid));
    }

    // PaymentEntity 저장 메서드
    public void save(PaymentEntity paymentEntity) {
        paymentRepository.save(paymentEntity);
    }

}


