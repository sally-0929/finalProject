package com.treasuredigger.devel.service;

import com.treasuredigger.devel.dto.CartItemDto;
import com.treasuredigger.devel.entity.Cart;
import com.treasuredigger.devel.entity.CartItem;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.repository.CartItemRepository;
import com.treasuredigger.devel.repository.CartRepository;
import com.treasuredigger.devel.repository.ItemRepository;
import com.treasuredigger.devel.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import com.treasuredigger.devel.dto.CartDetailDto;
import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.util.StringUtils;
import com.treasuredigger.devel.dto.CartOrderDto;
import com.treasuredigger.devel.dto.OrderDto;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String mid){

        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByMid(mid);

        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String mid){

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByMid(mid);
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String mid){
        Member curMember = memberRepository.findByMid(mid);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                            .findById(cartOrderDto.getCartItemId())
                            .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                            .findById(cartOrderDto.getCartItemId())
                            .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }

    //장바구니 총 개수 계산
    @Transactional(readOnly = true)
    public int getTotalCount(String mid) {
        Member member = memberRepository.findByMid(mid);
        System.out.println(member);
        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            return 0; // 장바구니가 없으면 0 반환
        }

        List<CartDetailDto> cartDetailList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailList.stream()
                .mapToInt(CartDetailDto::getCount) // 각 CartDetailDto에서 count를 가져옴
                .sum(); // 총합 계산
    }

}