package com.treasuredigger.devel.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.treasuredigger.devel.dto.BidDto;
import com.treasuredigger.devel.dto.BidItemDto;
import com.treasuredigger.devel.entity.Bid;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.mapper.BidItemMapper;
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

    private final BidItemMapper bidItemMapper;

    @Transactional
    public Bid saveBid(String bidItemId, Long memberId, long bidRegPrice, String buyNowCheck) {
        Bid bid = new Bid();
        bid.setBidRegPrice(bidRegPrice);
        bid.setBidRegTime(LocalDateTime.now());

        BidItem bidItem = bidItemRepository.findById(bidItemId)
                .orElseThrow(() -> new EntityNotFoundException("Bid Item not found with id: " + bidItemId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));

        bid.setBidItem(bidItem);
        bid.setMember(member);
        bid.setBuyNowCheck(buyNowCheck);

        return bidRepository.save(bid);
    }

    public List<BidDto> getBidList(String bidItemId) {
            return bidItemMapper.getBidList(bidItemId);

     }

     public List<BidItemDto> getBidMyList(String created_by){
        return bidItemMapper.getBidMyList(created_by);
     }

    }
