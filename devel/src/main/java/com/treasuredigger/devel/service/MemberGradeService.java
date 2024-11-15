package com.treasuredigger.devel.service;

import com.treasuredigger.devel.constant.OrderStatus;
import com.treasuredigger.devel.dto.MemberGradeDto;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.MemberGrade;
import com.treasuredigger.devel.constant.MemberGradeStatus;
import com.treasuredigger.devel.mapper.MemberMapper;
import com.treasuredigger.devel.repository.MemberGradeRepository;
import com.treasuredigger.devel.repository.OrderRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberGradeService {

    private final MemberGradeRepository memberGradeRepository;
    private final MemberMapper memberMapper;
    private final OrderRepository orderRepository;

    public void incrementMgdesc(Member member) {
        MemberGrade memberGrade = memberGradeRepository.findByMember(member);
        if (memberGrade != null) {
            long totalPurchaseAmount = calculateTotalActivities(member);
            memberGrade.setMgdesc((int) totalPurchaseAmount);
            updateGradeStatus(memberGrade, totalPurchaseAmount);
            memberGradeRepository.save(memberGrade);
        }
    }

    private long calculateTotalActivities(Member member) {

        long totalPurchaseAmount = orderRepository.sumTotalByMemberAndOrderStatusNot(member, OrderStatus.CANCEL);

        System.out.println("총 구매 금액: " + totalPurchaseAmount);

        return totalPurchaseAmount;
    }

    public void updateGradeStatus(MemberGrade memberGrade, long totalPurchaseAmount) {
        if (totalPurchaseAmount >= 1_000_000) {
            memberGrade.setMemberGradeStatus(MemberGradeStatus.VVIP);
        } else if (totalPurchaseAmount >= 500_000) {
            memberGrade.setMemberGradeStatus(MemberGradeStatus.VIP);
        } else if (totalPurchaseAmount >= 200_000) {
            memberGrade.setMemberGradeStatus(MemberGradeStatus.GOLD);
        } else if (totalPurchaseAmount >= 100_000) {
            memberGrade.setMemberGradeStatus(MemberGradeStatus.SILVER);
        } else {
            memberGrade.setMemberGradeStatus(MemberGradeStatus.NORMAL);
        }
    }

    public void deductFromMgdesc(Member member, int deductionAmount) {
        MemberGrade memberGrade = memberGradeRepository.findByMember(member);
        if (memberGrade != null) {
            // mgdesc에서 차감된 금액을 뺀 새로운 값으로 업데이트
            int newMgdesc = memberGrade.getMgdesc() - deductionAmount;
            memberGrade.setMgdesc(newMgdesc > 0 ? newMgdesc : 0); // mgdesc는 0 이하로 내려가지 않도록 설정
            updateGradeStatus(memberGrade, newMgdesc);
            memberGradeRepository.save(memberGrade);
        }
    }

    public int calculateAmountForNextGrade(MemberGradeStatus currentGrade, int currentAmount) {
        return switch (currentGrade) {
            case NORMAL -> Math.max(100_000 - currentAmount, 0);
            case SILVER -> Math.max(200_000 - currentAmount, 0);
            case GOLD -> Math.max(500_000 - currentAmount, 0);
            case VIP -> Math.max(1_000_000 - currentAmount, 0);
            case VVIP -> 0;
        };
    }

    public MemberGradeDto getMemberGrade(Long memberId) {
        return memberMapper.getMemberGrade(memberId);
    }

}