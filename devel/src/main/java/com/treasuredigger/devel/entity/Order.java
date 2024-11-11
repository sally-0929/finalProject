package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private OrderStatus orderStatus; //주문상태

    @Column(name = "total_amount")
    private int totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PaymentEntity> payments = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);

        for(OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }

        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(order.getTotalPrice());
        return order;
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;
        this.totalAmount = 0;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    // 주문에 포함된 모든 Item을 반환하는 메서드 추가
    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            items.add(orderItem.getItem());  // OrderItem에서 Item 객체를 가져옴
        }
        return items;
    }

    // 결제 내역 추가
    public void addPayment(PaymentEntity paymentEntity) {
        this.payments.add(paymentEntity);
        paymentEntity.setOrder(this);  // 결제 내역과 주문 연결
    }
}
