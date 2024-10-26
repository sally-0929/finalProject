package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

}
