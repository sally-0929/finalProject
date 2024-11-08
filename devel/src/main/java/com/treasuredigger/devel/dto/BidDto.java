package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter@Setter
public class BidDto {

    private Long bino;

    private long bidRegPrice;



    private BidItem bidItem;

    private String createdBy;

    private LocalDateTime regtime;

    private String bidItemId;

    private long rownum;

    private String email;
    private String mid;


    private Member member;

    public BidDto(){}

    public BidDto(String createdBy, long bidRegPrice) {
        this.createdBy = createdBy;
        this.bidRegPrice = bidRegPrice;
    }
}
