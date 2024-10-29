package com.treasuredigger.devel.service;

import com.treasuredigger.devel.dto.MemberGradeDto;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.MemberGrade;
import com.treasuredigger.devel.constant.MemberGradeStatus;
import com.treasuredigger.devel.mapper.MemberMapper;
import com.treasuredigger.devel.repository.MemberGradeRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberGradeService {

    private final MemberGradeRepository memberGradeRepository;
    private final MemberMapper memberMapper;

    public void updateMgdesc(MemberGrade memberGrade, int newMgdesc) {
        memberGrade.setMgdesc(newMgdesc);
        checkAndUpdateGradeStatus(memberGrade);
    }

    private void checkAndUpdateGradeStatus(MemberGrade memberGrade) {
        if (memberGrade.getMgdesc() >= 50) {
            memberGrade.setMemberGradeStatus(MemberGradeStatus.SILVER);
        }
        if (memberGrade.getMgdesc() >= 100) {
            memberGrade.setMemberGradeStatus(MemberGradeStatus.GOLD);
        }
    }

//    public MemberGradeStatus getMemberGradeStatus(Member member) {
//        MemberGrade memberGrade = memberGradeRepository.findByMember(member); // MemberGrade 조회
//        if (memberGrade != null) {
//            return memberGrade.getMemberGradeStatus();
//        }
//        return MemberGradeStatus.NORMAL; // 기본 값
//    }

    public MemberGradeDto getMemberGrade(Long memberId) {
        return memberMapper.getMemberGrade(memberId);
    }

    public void incrementMgdesc(Member member) {
        MemberGrade memberGrade = memberGradeRepository.findByMember(member);
        if (memberGrade != null) {
            memberGrade.setMgdesc(memberGrade.getMgdesc() + 1);
            checkAndUpdateGradeStatus(memberGrade);
            memberGradeRepository.save(memberGrade); // 변경된 멤버 등급 저장
        }
    }
}