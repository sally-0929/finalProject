package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.BidItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidItemRepository extends JpaRepository<BidItem, String> {

    long countByBidItemIdStartingWith(String prefix);

    BidItem findByBidItemId(String bidItemId);
}
