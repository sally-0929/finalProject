package com.treasuredigger.devel.service;

import com.treasuredigger.devel.dto.OrderDto;
import com.treasuredigger.devel.entity.*;
import com.treasuredigger.devel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.treasuredigger.devel.dto.OrderHistDto;
import com.treasuredigger.devel.dto.OrderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.thymeleaf.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;

    private final MemberRepository memberRepository;

    private final OrderRepository orderRepository;

    private final ItemImgRepository itemImgRepository;

    private final MemberGradeService memberGradeService;

    private final BidItemRepository bidItemRepository;

    private final BidItemImgRepository bidItemImgRepository;

    public Long order(OrderDto orderDto, String mid){

        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByMid(mid);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);
        // 주문 후 회원 등급 갱신
        memberGradeService.incrementMgdesc(member);
        return order.getId();
    }

    public Long orderBidItem(String bidItemId, String mid) {
        BidItem bidItem = bidItemRepository.findById(bidItemId) .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByMid(mid);
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderBidItem(bidItem);
        orderItemList.add(orderItem);
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);
        // 주문 후 회원 등급 갱신
         memberGradeService.incrementMgdesc(member);
         return order.getId();

        }


    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getItem() != null) {
                    ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                    OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                    orderHistDto.addOrderItemDto(orderItemDto);
                } else if (orderItem.getBiditem() != null) {
                    BidItemImg bidItemImg = bidItemImgRepository.findByBidItem_BidItemIdAndBidRepimgYn(orderItem.getBiditem().getBidItemId(), "Y");
                    OrderItemDto orderItemDto = new OrderItemDto(orderItem, bidItemImg != null ? bidItemImg.getBidImgUrl() : null);
                    orderHistDto.addOrderItemDto(orderItemDto);
                } else {
                    // 둘 다 null인 경우 처리 (필요에 따라 로깅 또는 예외 처리 추가 가능)
                    // 예를 들어, 로깅
                    // log.warn("OrderItem has neither Item nor BidItem: " + orderItem.getId());
                }
            }
            orderHistDtos.add(orderHistDto);
            System.out.println("orderHistDto" + orderHistDto.toString());
        }

        return new PageImpl<>(orderHistDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String mid){
        Member curMember = memberRepository.findByMid(mid);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        int orderTotalPrice = order.getTotalPrice();
        order.cancelOrder();

        Member member = order.getMember();
        memberGradeService.deductFromMgdesc(member, orderTotalPrice);
    }

    public Long orders(List<OrderDto> orderDtoList, String mid){

        Member member = memberRepository.findByMid(mid);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);
        // 주문 후 회원 등급 갱신
        memberGradeService.incrementMgdesc(member);

        return order.getId();
    }

    public BidItem getBidItemByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId) .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + orderId));
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getBiditem() != null) {
                return orderItem.getBiditem();
            }
        } throw new EntityNotFoundException("No BidItem found for order id " + orderId);
    }

}