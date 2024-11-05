package com.treasuredigger.devel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_idx")
    private Long paymentIdx;  // 결제 번호 (Primary Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;  // 주문 번호 (외래 키, Order와의 관계 설정)

    @Column(name = "payment_amount")
    private int paymentAmount;  // 결제 금액

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;  // 결제 일시

    public Payment() {
        this.paymentDate = LocalDateTime.now();  // 기본값은 현재 시각
    }

    // 생성자
    public Payment(Order order, int paymentAmount) {
        this.order = order;
        this.paymentAmount = paymentAmount;
        this.paymentDate = LocalDateTime.now();  // 결제 일시는 현재 시각
    }

    // Getter, Setter
    public Long getPaymentIdx() {
        return paymentIdx;
    }

    public void setPaymentIdx(Long paymentIdx) {
        this.paymentIdx = paymentIdx;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(int paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentIdx=" + paymentIdx +
                ", order=" + order +
                ", paymentAmount=" + paymentAmount +
                ", paymentDate=" + paymentDate +
                '}';
    }
}