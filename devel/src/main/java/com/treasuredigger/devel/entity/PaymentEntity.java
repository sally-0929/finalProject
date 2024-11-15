package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.constant.PaymentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;  // 어떤 주문에 속하는지

    private int amount;  // 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PaymentStatus paymentStatus;  // 결제 상태 (예: 성공, 실패, 대기 중 등)

    private LocalDateTime paymentDate;  // 결제일

    @Column(name = "merchant_uid", nullable = false)
    private String merchantUid;  // 가맹점 주문 ID

    // 기본 생성자
    public PaymentEntity() {
    }

    // 모든 필드를 초기화하는 생성자
    public PaymentEntity(Order order, int amount, PaymentStatus paymentStatus) {
        this.order = order;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentDate = LocalDateTime.now();
    }

    // Getter, Setter 메서드

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    // 결제 완료 시간 설정 메서드 (현재 시간으로 설정)
    public void setPaymentDateNow() {
        this.paymentDate = LocalDateTime.now();
    }

    public void setMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
    }

    public String getMerchantUid(){
        return merchantUid;
    }

    public void cancelPayment() {
        this.paymentStatus = PaymentStatus.CANCELED;  // 결제 상태를 CANCELED로 변경
        this.paymentDate = LocalDateTime.now();        // 결제 취소 일시를 현재 시간으로 설정
    }
}
