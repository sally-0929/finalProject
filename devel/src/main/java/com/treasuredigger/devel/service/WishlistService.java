package com.treasuredigger.devel.service;

import com.treasuredigger.devel.dto.BidItemDto;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.Wishlist;
import com.treasuredigger.devel.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    public boolean isWishlisted(BidItem bidItem, Member member) {
        return wishlistRepository.findByBidItemAndMember(bidItem, member).isPresent();
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
}
