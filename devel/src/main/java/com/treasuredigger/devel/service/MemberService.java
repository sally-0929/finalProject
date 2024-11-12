package com.treasuredigger.devel.service;

import com.treasuredigger.devel.constant.Role;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.Order;
import com.treasuredigger.devel.repository.MemberRepository;
import com.treasuredigger.devel.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    private final OrderRepository orderRepository;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByMid(member.getMid());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 아이디입니다. 새로운 아이디로 입력해주세요.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String mid) throws UsernameNotFoundException {
        Member member = memberRepository.findByMid(mid);
        if (member == null) {
            throw new UsernameNotFoundException(mid);
        }

        return User.builder()
                .username(member.getMid())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

    public Member findMemberByMid(String mid) {
        return memberRepository.findByMid(mid);
    }

    public Member updateMember(String mid, Member member) {
        Member memberUpdate = findMemberByMid(mid);
        memberUpdate.setName(member.getName());
        memberUpdate.setEmail(member.getEmail());
        memberUpdate.setAddress(member.getAddress());
        memberUpdate.setPhone(member.getPhone());
        return memberRepository.save(memberUpdate);
    }

    public void deleteMember(String mid) {
        Member member = findMemberByMid(mid);

        // 연관된 주문의 회원 정보를 null로 설정
        List<Order> orders = member.getOrders();
        for (Order order : orders) {
            order.setMember(null); orderRepository.save(order);
        }

        if (member == null) {
            throw new IllegalStateException("존재하지 않는 회원입니다.");
        }
        memberRepository.delete(member);
    }

    public Member findNormalMemberByEmail(String email){
        return memberRepository.findNormalByEmail(email);
    }

    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public void verifyEmail(String mid) {
        Member member = memberRepository.findByMid(mid);
        if (member == null) {
            throw new IllegalStateException("존재하지 않는 회원입니다.");
        }
        if (member.isEmailVerified()) {
            throw new IllegalStateException("이미 인증된 이메일입니다.");
        }
        member.setEmailVerified(true);
        memberRepository.save(member);
    }

    public void verifyPhone(String mid) {
        Member member = memberRepository.findByMid(mid);
        member.setPhoneVerified(true);
        memberRepository.save(member);
    }

    public void updateMemberRole(String mid) {
        Member member = memberRepository.findByMid(mid);
        if (member != null) {
            member.setRole(Role.MEMBER);
            memberRepository.save(member);
        }
    }
}
