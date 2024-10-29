package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import com.treasuredigger.devel.dto.CartDetailDto;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    @Query("select new com.treasuredigger.devel.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl, i.stockNumber) " +
            "from CartItem ci " +
            "join ci.item i " +
            "join ItemImg im on im.item.id = i.id " +
            "where ci.cart.id = :cartId " +
            "and im.repimgYn = 'Y' " +
            "order by ci.regTime desc")
    List<CartDetailDto> findCartDetailDtoList(Long cartId);

}