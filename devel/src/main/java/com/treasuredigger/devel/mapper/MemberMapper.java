package com.treasuredigger.devel.mapper;

import com.treasuredigger.devel.dto.BidItemDto;
import com.treasuredigger.devel.dto.MemberGradeDto;
import com.treasuredigger.devel.dto.WishlistDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {

    MemberGradeDto getMemberGrade(@Param("memberId") Long memberId);

    List<WishlistDto> getBidMyList(@Param("createdBy") String createdBy);
}