package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@Table(name = "Payment")
public class PaymentEntity {

    @Id
    @GeneratedValue
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String impUid;  // Iamport 고유 ID
    private String merchantUid;  // 가맹점 주문 ID
    private int amount;  // 결제 금액

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;  // 결제 상태 (성공, 실패 등)

    private LocalDateTime paidAt;  // 결제 완료 시간

}
