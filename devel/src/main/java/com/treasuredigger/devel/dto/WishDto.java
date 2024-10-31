package com.treasuredigger.devel.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
public class WishDto {
    private long id;
    private String bidItemId;
    private long itemId;
    private long memberId;
}
