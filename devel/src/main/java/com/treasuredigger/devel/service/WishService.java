package com.treasuredigger.devel.service;

import com.treasuredigger.devel.dto.WishDto;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.Wishlist;
import com.treasuredigger.devel.repository.BidItemRepository;
import com.treasuredigger.devel.repository.ItemRepository;
import com.treasuredigger.devel.repository.MemberRepository;
import com.treasuredigger.devel.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WishService {
    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final BidItemRepository bidItemRepository;

    public void saveWish(WishDto wishDto) {
        // Member 설정
        Member member = memberRepository.findById(wishDto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Item 설정 (null이 아닌 경우)
        Item item = null;
        if (wishDto.getItemId() != 0) {
            item = itemRepository.findById(wishDto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));
        }

        // BidItem 설정 (null이 아닌 경우)
        BidItem bidItem = null;
        if (wishDto.getBidItemId() != null) {
            bidItem = bidItemRepository.findById(wishDto.getBidItemId())
                    .orElseThrow(() -> new RuntimeException("BidItem not found"));
        }

        Wishlist wishlist = Wishlist.fromDto(wishDto, member, item, bidItem);

        System.out.println("wishList : ++++" + wishlist);
        wishRepository.save(wishlist);
    }
}
