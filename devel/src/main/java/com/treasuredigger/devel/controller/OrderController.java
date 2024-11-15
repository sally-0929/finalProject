package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.OrderDto;
import com.treasuredigger.devel.entity.OrderItem;
import com.treasuredigger.devel.entity.PaymentEntity;
import com.treasuredigger.devel.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

import com.treasuredigger.devel.dto.OrderHistDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto
            , BindingResult bindingResult, Principal principal){


        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long orderId;

        try {
            orderId = orderService.order(orderDto, email);
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model) {
        System.out.println("prin value" + principal.getName());

        // 페이지 정보 설정
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 20);

        // 주문 내역과 결제 정보 가져오기
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);
        System.out.println("orderHist" + ordersHistDtoList.toString());

        // 모델에 주문 내역과 결제 정보 추가
        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        model.addAttribute("orderStatusOrder", com.treasuredigger.devel.constant.OrderStatus.ORDER);

        // 결제 정보도 모델에 포함
        ordersHistDtoList.forEach(orderHistDto -> {
            PaymentEntity payment = orderHistDto.getPaymentEntity();
//            model.addAttribute("payment_" + orderHistDto.getOrderId(), payment); // 개별 주문에 대해 결제 정보 모델에 추가
            System.out.println("------------------------");
            System.out.println(orderHistDto.getOrderId());
            System.out.println(payment);
        });

        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity<?> cancelOrder(@PathVariable("orderId") Long orderId , Principal principal){

        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}