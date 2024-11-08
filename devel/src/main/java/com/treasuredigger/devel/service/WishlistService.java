package com.treasuredigger.devel.service;

import com.treasuredigger.devel.dto.BidItemDto;
import com.treasuredigger.devel.dto.ItemFormDto;
import com.treasuredigger.devel.dto.WishlistDto;
import com.treasuredigger.devel.entity.*;
import com.treasuredigger.devel.mapper.MemberMapper;
import com.treasuredigger.devel.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemImgRepository itemImgRepository;

    @Autowired
    private BidItemImgRepository bidItemImgRepository;

    @Autowired
    private BidItemRepository bidItemRepository;



    public boolean isWishlisted(BidItem bidItem, Member member) {
        System.out.println("bid service");
        return wishlistRepository.findByBidItemAndMember(bidItem, member).isPresent();
    }
    public boolean isWishlisted(String bidItemId, String userId) {
        Member member = memberRepository.findByMid(userId);
        if (member == null) return false;

        BidItem bidItem = bidItemRepository.findByBidItemId(bidItemId);
        return wishlistRepository.findByBidItemAndMember(bidItem, member).isPresent();
    }
    public boolean isWishlisted(Item item, Member member) {
        System.out.println("item sservice");
        return wishlistRepository.findByItemAndMember(item, member).isPresent();
    }



    public void toggleWishlist(BidItem bidItem, Member member) {
        Optional<Wishlist> wishlist = wishlistRepository.findByBidItemAndMember(bidItem, member);
        if (wishlist.isPresent()) {
            wishlistRepository.delete(wishlist.get());
        } else {
            Wishlist newWishlist = new Wishlist();
            newWishlist.setBidItem(bidItem);
            newWishlist.setMember(member);
            wishlistRepository.save(newWishlist);
        }
    }
    public void toggleWishlist(Item item, Member member) {
        Optional<Wishlist> wishlist = wishlistRepository.findByItemAndMember(item, member);
        if (wishlist.isPresent()) {
            wishlistRepository.delete(wishlist.get());
        } else {
            Wishlist newWishlist = new Wishlist();
            newWishlist.setItem(item);
            newWishlist.setMember(member);
            wishlistRepository.save(newWishlist);
        }
    }

    public List<WishlistDto> getWishlistByMember(String id) {
        return memberMapper.getBidMyList(id);
    }

    public List<WishlistDto> getRecentItemWishlistByMember(String memberId, int limit) {
        Member member = memberRepository.findByMid(memberId);
        if (member == null) {
            return Collections.emptyList();
        }

        List<Wishlist> wishlists = wishlistRepository.findTop3ByMemberAndItemIsNotNullOrderByIdDesc(member);

        return wishlists.stream()
                .map(wishlist -> {
                    WishlistDto dto = new WishlistDto();
                    dto.setItemId(wishlist.getItem().getId());
                    dto.setItemNm(wishlist.getItem().getItemNm());

                    List<ItemImg> itemImages = itemImgRepository.findByItemIdOrderByIdAsc(wishlist.getItem().getId());
                    if (!itemImages.isEmpty()) {
                        dto.setImgUrl(itemImages.get(0).getImgUrl());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<WishlistDto> getRecentBidWishlistByMember(String memberId, int limit) {
        Member member = memberRepository.findByMid(memberId);
        if (member == null) {
            return Collections.emptyList();
        }

        List<Wishlist> wishlists = wishlistRepository.findTop3ByMemberAndBidItemIsNotNullOrderByIdDesc(member);

        return wishlists.stream()
                .map(wishlist -> {
                    WishlistDto dto = new WishlistDto();
                    dto.setBidItemId(wishlist.getBidItem().getBidItemId());
                    dto.setBidItemName(wishlist.getBidItem().getBidItemName());

                    List<BidItemImg> bidItemImages = bidItemImgRepository.findByBidItem_BidItemIdOrderByIdAsc(wishlist.getBidItem().getBidItemId());
                    if (!bidItemImages.isEmpty()) {
                        dto.setImgUrl(bidItemImages.get(0).getBidImgUrl());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
    public List<Long> getWishlistItemIdsByMember(String userId) {
        Member member = memberRepository.findByMid(userId);
        if (member == null) return Collections.emptyList();

        return wishlistRepository.findByMember(member)
                .stream()
                .filter(wishlist -> wishlist.getItem() != null) // item이 null인지 확인
                .map(wishlist -> wishlist.getItem().getId())
                .collect(Collectors.toList());
    }
}

