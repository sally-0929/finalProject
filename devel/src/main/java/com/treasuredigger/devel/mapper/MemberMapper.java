package com.treasuredigger.devel.mapper;

import com.treasuredigger.devel.dto.MemberGradeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    MemberGradeDto getMemberGrade(@Param("memberId") Long memberId);
}