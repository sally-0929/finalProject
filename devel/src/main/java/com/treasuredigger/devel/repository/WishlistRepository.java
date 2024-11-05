package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.Wishlist;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.BidItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByItemAndMember(Item item, Member member);
    Optional<Wishlist> findByBidItemAndMember(BidItem bidItem, Member member);
}
