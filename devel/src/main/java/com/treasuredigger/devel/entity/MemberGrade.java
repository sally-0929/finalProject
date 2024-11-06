package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.constant.MemberGradeStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member_grade_tbl")
public class MemberGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mgid;

    @Enumerated(EnumType.STRING)
    private MemberGradeStatus memberGradeStatus;

    private int mgdesc;

    @OneToOne
    @JoinColumn(name = "member_id" )
    private Member member;

    public MemberGrade(Member member) {
        this.member = member;
        this.memberGradeStatus = MemberGradeStatus.NORMAL; // 기본 등급 설정
        this.mgdesc = 0;
    }
}
