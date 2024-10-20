package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.BidItemImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidItemImgRepository extends JpaRepository<BidItemImg, Long> {
    List<BidItemImg> findByBidItem_BidItemIdOrderByIdAsc(String bidItemId);
    BidItemImg findByBidItem_BidItemIdAndBidRepimgYn(String bidItemId, String repimgYn);
}
