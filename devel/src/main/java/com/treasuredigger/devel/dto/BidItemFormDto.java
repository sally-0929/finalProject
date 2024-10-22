package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.constant.ItemStatus;
import com.treasuredigger.devel.entity.BaseEntity;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.ItemCategory;
import com.treasuredigger.devel.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BidItemFormDto{

    private String bidItemId;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String bidItemName;

    @NotBlank(message = "상품 설명은 필수 입력 값입니다.")
    private String bidItemDesc;

    @NotNull(message = "시작 가격은 필수 입력 값입니다.")
    private Long startPrice;

    @NotNull(message = "즉시 구매 가격은 필수 입력 값입니다.")
    private Long maxPrice;

    private LocalDateTime bidStartDate;

    private LocalDateTime bidEndDate;

    @NotNull(message = "상품 상태는 필수 입력 값입니다.")
    private ItemStatus itemStatus;

    private String memberId;

    @NotBlank(message = "카테고리 ID는 필수 입력 값입니다.")
    private String cid;

    private List<BIdItemImgDto> BiditemImgDtoList = new ArrayList<>();

    private List<Long> BiditemImgIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    public BidItem createBidItem() {
        return modelMapper.map(this, BidItem.class);
    }

    public static BidItemFormDto of(BidItem bidItem) {
        return modelMapper.map(bidItem, BidItemFormDto.class);
    }


    public BidItem createBidItem(Member member, ItemCategory itemCategory) {
        BidItem bidItem = modelMapper.map(this, BidItem.class);
        bidItem.setMember(member);
        bidItem.setItemCategory(itemCategory);
        return bidItem;
    }


}
