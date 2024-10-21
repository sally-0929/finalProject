package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.constant.ItemStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

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




}

