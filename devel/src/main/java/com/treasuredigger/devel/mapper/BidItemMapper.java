package com.treasuredigger.devel.mapper;
import com.treasuredigger.devel.dto.BidDto;
import com.treasuredigger.devel.dto.BidItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface BidItemMapper {

    List<BidItemDto> selectBidList(@Param("searchQuery") String searchQuery, @Param("pageable") Pageable pageable, @Param("cid") String cid, @Param("auctionStatus") String auctionStatus);
    BidItemDto selectBidItemById(@Param("bidItemId") String bidItemId);
    int countBidItems(@Param("searchQuery") String searchQuery,  @Param("cid") String cid ,@Param("auctionStatus") String auctionStatus);

    List<BidDto> getBidList(@Param("bidItemId")String bidItemId);

    List<BidItemDto> getBidMyList(@Param("createdBy") String created_by);

    BidDto getSuccessfulBid(@Param("bidItemId")String bidItemId);

}
