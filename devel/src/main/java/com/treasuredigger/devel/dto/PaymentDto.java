package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.constant.PaymentStatus;
import com.treasuredigger.devel.entity.Order;
import com.treasuredigger.devel.entity.PaymentEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    private Long paymentId;
    private Long memberId;  // 회원 정보
    private Long productId; // 상품 정보
    private Long orderId;   // 주문 정보
    private String impUid;  // 아임포트 고유 ID
    private String merchantUid;  // 가맹점 주문 ID
    private int amount;  // 결제 금액
    private Order order;
    private PaymentStatus status;  // 결제 상태
    private LocalDateTime paidAt;  // 결제 완료 시간

    public PaymentEntity toEntity() {
        return PaymentEntity.builder()
                .id(paymentId)
                .order(order)
                .impUid(impUid)
                .merchantUid(merchantUid)
                .amount(amount)
                .status(status)
                .paidAt(paidAt)
                .build();
    }
}

