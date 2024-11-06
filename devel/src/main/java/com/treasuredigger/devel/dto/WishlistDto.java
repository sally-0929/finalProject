package com.treasuredigger.devel.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WishlistDto {

    private Long itemId;
    private String bidItemId;
    private String bidItemName;
    private String itemStatus;
    private String itemNm;
    private String iSellStatus;
    private Integer price;
    private Integer stockNumber;
    private Integer maxPrice;
    private Integer bidNowPrice;
    private String imgUrl;

}
