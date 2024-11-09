package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.constant.ItemStatus;
import com.treasuredigger.devel.entity.BidItem;
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
    private String email;


    private String cname;
    private String buyNowCheck;

    private LocalDateTime regTime;
    private String createBy;
    private long bidRegPrice;
    private boolean wishlisted = false;

    private List<BIdItemImgDto> biditemImgDtoList = new ArrayList<>();

    public BidItem convertDtoToEntity(BidItemDto bidItemDto) {
        BidItem bidItem = new BidItem();
        bidItem.setBidItemId(bidItemDto.getBidItemId());
        bidItem.setBidItemName(bidItemDto.getBidItemName());
        bidItem.setBidItemDesc(bidItemDto.getBidItemDesc());
        bidItem.setStartPrice(bidItemDto.getStartPrice());
        bidItem.setMaxPrice(bidItemDto.getMaxPrice());
        bidItem.setBidStartDate(bidItemDto.getBidStartDate());
        bidItem.setBidEndDate(bidItemDto.getBidEndDate());
        bidItem.setItemStatus(ItemStatus.valueOf(bidItemDto.getItemStatus()));
        return bidItem;
        }
    }

