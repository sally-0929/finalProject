package com.treasuredigger.devel.mapper;
import com.treasuredigger.devel.dto.BidItemDto;
import com.treasuredigger.devel.entity.BidItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface BidItemMapper {

    List<BidItemDto> selectBidList(@Param("pageable") Pageable pageable);

     int countBidItems();

}
