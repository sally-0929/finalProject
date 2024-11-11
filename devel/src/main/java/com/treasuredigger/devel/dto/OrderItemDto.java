package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemDto {

    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        System.out.println("Order Item" + orderItem);
        if (orderItem.getItem() != null) {
            this.orderId = orderItem.getId();
            this.itemId = orderItem.getItem().getId();
            this.itemNm = orderItem.getItem().getItemNm();
            this.count = orderItem.getCount();
            this.orderPrice = orderItem.getOrderPrice();
            this.imgUrl = imgUrl;
        } else if (orderItem.getBiditem() != null) {
            this.orderId = orderItem.getId();
            this.bidItemId = orderItem.getBiditem().getBidItemId();
            this.itemNm = orderItem.getBiditem().getBidItemName();
            this.count = orderItem.getCount();
            this.orderPrice = orderItem.getOrderPrice();
            this.imgUrl = imgUrl;
        } else {
            // 둘 다 null인 경우 처리 (필요에 따라 로깅 또는 예외 처리 추가 가능)
            // 예를 들어, 로깅
            // log.warn("OrderItem has neither Item nor BidItem: " + orderItem.getId());
        }
    }
    private long orderId;
    private String bidItemId;
    private Long itemId; // 상품 ID
    private String itemNm; //상품명
    private int count; //주문 수량
    private int orderPrice; //주문 금액
    private String imgUrl; //상품 이미지 경로
}
