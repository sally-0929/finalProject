package com.treasuredigger.devel.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import lombok.ToString;

@Entity
@Getter @Setter
@ToString
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid_item_id")
    private BidItem biditem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문가격

    private int count; //수량

    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice());
        item.removeStock(count);
        return orderItem;
    }

    public static OrderItem createOrderBidItem(BidItem bidItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBiditem(bidItem);
        orderItem.setCount(1);
        orderItem.setOrderPrice((int) bidItem.getMaxPrice());

        return orderItem;
    }

    public int getTotalPrice(){
        return orderPrice*count;
    }

    public void cancel() {
        if(this.getItem() != null){
            getItem().addStock(count);
        }else if(this.getBiditem() != null){

        }
    }

    // 경매 상품인지 일반 상품인지 확인하는 메서드
    public boolean isBidItem() {
        return biditem != null;
    }

}