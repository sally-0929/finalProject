package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.constant.MemberGradeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberGradeDto {

    private int mgid; // 등급 ID
    private MemberGradeStatus memberGradeStatus; // 회원 등급 상태
    private int mgdesc; // 등급 설명
    private Long memberId; // 회원 ID
}