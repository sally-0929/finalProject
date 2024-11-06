package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.constant.PaymentStatus;
import com.treasuredigger.devel.entity.Order;
import com.treasuredigger.devel.entity.Payment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentDto {
    Long paymentId;
    Order order;
    String impUid;
    String merchantUid;
    int amount;
    PaymentStatus status;
    LocalDateTime paidAt;

    public Payment toEntity() {
        return Payment.builder()
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
