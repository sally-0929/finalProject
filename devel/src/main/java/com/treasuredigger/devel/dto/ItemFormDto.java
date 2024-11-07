package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.constant.ItemSellStatus;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.entity.ItemCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString
public class ItemFormDto{

    private Long id;

    private String sellerId; // 판매자 ID
    private String sellerRole; // 판매자 권한

    @NotBlank(message = "카테고리 ID는 필수 입력 값입니다.")
    private String cid;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotBlank(message = "상품 상세는 필수 입력 값입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stockNumber;

    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    private String cname;

    private ItemSellStatus itemSellStatus = ItemSellStatus.SELL;
    //private ItemSellStatus itemSellStatus;

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    private List<Long> itemImgIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem(ItemCategory itemCategory) { // 카테고리를 인자로 받도록 수정
        Item item = modelMapper.map(this, Item.class);
        item.setItemCategory(itemCategory); // 카테고리 설정
        return item;
    }

    public Item toEntity() { return modelMapper.map(this, Item.class); }

    public static ItemFormDto of(Item item){
        return modelMapper.map(item,ItemFormDto.class);
    }

}