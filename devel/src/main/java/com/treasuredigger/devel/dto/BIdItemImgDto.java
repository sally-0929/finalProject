package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.entity.BidItemImg;
import com.treasuredigger.devel.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class BIdItemImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    private static ModelMapper modelMapper = new ModelMapper();

    public static BIdItemImgDto of(BidItemImg BiditemImg) {
        return modelMapper.map(BiditemImg, BIdItemImgDto.class);
    }

}