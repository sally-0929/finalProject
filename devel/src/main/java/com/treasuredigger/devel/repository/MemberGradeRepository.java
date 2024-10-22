package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.MemberGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberGradeRepository extends JpaRepository<MemberGrade, Integer> {
    MemberGrade findByMember(Member member); // 회원에 해당하는 등급 조회
}