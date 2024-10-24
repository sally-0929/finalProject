package com.treasuredigger.devel.service;

import java.time.LocalDateTime;

import com.treasuredigger.devel.entity.Bid;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.repository.BidItemRepository;
import com.treasuredigger.devel.repository.BidRepository;
import com.treasuredigger.devel.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final BidItemRepository bidItemRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Bid saveBid(String bidItemId, Long memberId, long bidRegPrice) {
        Bid bid = new Bid();
        bid.setBidRegPrice(bidRegPrice);
        bid.setBidRegTime(LocalDateTime.now());

        BidItem bidItem = bidItemRepository.findById(bidItemId)
                .orElseThrow(() -> new EntityNotFoundException("Bid Item not found with id: " + bidItemId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));

        bid.setBidItem(bidItem);
        bid.setMember(member);

        return bidRepository.save(bid);
    }
}
