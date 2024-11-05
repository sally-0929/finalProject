package com.treasuredigger.devel.service;

import com.treasuredigger.devel.dto.BidItemDto;
import com.treasuredigger.devel.dto.ItemFormDto;
import com.treasuredigger.devel.dto.WishlistDto;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.Wishlist;
import com.treasuredigger.devel.mapper.MemberMapper;
import com.treasuredigger.devel.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private MemberMapper memberMapper;



    public boolean isWishlisted(BidItem bidItem, Member member) {
        System.out.println("bid service");
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



}
