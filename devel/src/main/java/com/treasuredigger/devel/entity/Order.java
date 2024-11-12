package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.constant.OrderStatus;
import com.treasuredigger.devel.constant.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private OrderStatus orderStatus; // 주문 상태

    @Column(name = "total_amount")
    private int totalAmount;

    @Column(name = "merchant_uid", unique = true)
    private String merchantUid; // 가맹점 주문 ID (merchantUid 추가)

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PaymentEntity> payments = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        String merchantUid = UUID.randomUUID().toString().replace("-", "");

        Order order = new Order();
        order.setMember(member);

        for (OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }

        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(order.getTotalPrice());
        order.setMerchantUid(merchantUid); // merchantUid 설정

        return order;
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
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

    public void addPayment(PaymentEntity paymentEntity) {
        this.payments.add(paymentEntity);
        paymentEntity.setOrder(this);  // 결제 내역과 주문 연결
    }

    // 결제 내역 추가
    public void addPayment(int paymentAmount, PaymentStatus paymentStatus) {
        PaymentEntity payment = new PaymentEntity(this, paymentAmount, paymentStatus);
        this.addPayment(payment);  // Order의 payments에 추가
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", member=" + (member != null ? member.getId() : "null") +  // member가 null일 수 있기 때문에 null 체크
                ", orderDate=" + orderDate +
                ", orderStatus=" + orderStatus +
                ", totalAmount=" + totalAmount +
                ", merchantUid='" + merchantUid + "'" +  // merchantUid 추가
                ", orderItems=" + orderItems.size() + " items" +  // orderItems의 개수만 표시
                ", payments=" + payments.size() + " payments" +  // payments의 개수만 표시
                '}';
    }
}

