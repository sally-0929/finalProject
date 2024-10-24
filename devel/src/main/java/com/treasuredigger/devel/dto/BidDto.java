package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class BidDto {

    private Long bino;

    private long bidRegPrice;



    private BidItem bidItem;

    private Member member;
}
