package com.treasuredigger.devel.mapper;
import com.treasuredigger.devel.dto.BidItemDto;
import com.treasuredigger.devel.entity.BidItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BidItemMapper {

    public List<BidItemDto> selectBidList();

}
