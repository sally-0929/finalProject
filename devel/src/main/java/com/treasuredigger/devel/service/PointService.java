package com.treasuredigger.devel.service;

import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PointService {

    private final MemberRepository memberRepository;  // Member 엔티티 저장소

    /**
     * 포인트를 증가시킵니다.
     * @param memberId 회원 ID
     * @param amount 증가할 금액 (BigDecimal)
     */
    public void addPoints(Long memberId, BigDecimal amount) {
        // BigDecimal을 int로 변환 (소수점 버림)
        int pointAmount = amount.intValue();  // 소수점 이하 버리고 정수 부분만 사용

        if (pointAmount > 0) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

            member.addPoints(pointAmount);  // 포인트를 증가

            memberRepository.save(member);  // 변경된 회원 정보 저장
        } else {
            throw new IllegalArgumentException("포인트는 0보다 큰 값이어야 합니다.");
        }
    }

    /**
     * 포인트를 차감합니다.
     * @param memberId 회원 ID
     * @param amount 차감할 금액 (BigDecimal)
     */
    public void deductPoints(Long memberId, BigDecimal amount) {
        // BigDecimal을 int로 변환 (소수점 버림)
        int pointAmount = amount.intValue();  // 소수점 이하 버리고 정수 부분만 사용

        if (pointAmount > 0) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

            member.deductPoints(pointAmount);  // 포인트를 차감

            memberRepository.save(member);  // 변경된 회원 정보 저장
        } else {
            throw new IllegalArgumentException("포인트는 0보다 큰 값이어야 합니다.");
        }
    }
}
