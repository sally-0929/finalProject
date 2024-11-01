package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.dto.BidItemDto;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByBidItemAndMember(BidItem bidItem, Member member);
}

