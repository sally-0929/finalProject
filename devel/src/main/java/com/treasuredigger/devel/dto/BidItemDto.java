package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.constant.ItemStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
@ToString
public class BidItemDto {

    private String bidItemId;
    private String bidItemName;
    private String bidItemDesc;
    private long startPrice;
    private long maxPrice;
    private LocalDateTime bidStartDate;
    private LocalDateTime bidEndDate;
    private String itemStatus;
    private String memberId;       // Member entity의 ID
    private String itemCategoryId;
    private String bidImgName;
    private String bidImgUrl;
    private String bidOriImgName;
    private long bidNowPrice;
    private String bidCount;
    private String mgId;
    private String mgst;
    private String mid;
    private String bino;

    private String cname;

    private LocalDateTime regTime;
    private String createBy;
    private long bidRegPrice;

    private List<BIdItemImgDto> biditemImgDtoList = new ArrayList<>();




}

