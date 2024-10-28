package com.treasuredigger.devel.service;

import com.treasuredigger.devel.constant.Role;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByMid(member.getMid());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String mid) throws UsernameNotFoundException {

        Member member = memberRepository.findByMid(mid);

        if(member == null){
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
        if (member == null) {
            throw new IllegalStateException("존재하지 않는 회원입니다.");
        }

        memberRepository.delete(member);
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public void verifyEmail(String mid){
        Member member = memberRepository.findByMid(mid);
        member.setEmailVerified(true);
        memberRepository.save(member);
    }

    public void verifyPhone(String mid){
        Member member = memberRepository.findByMid(mid);
        member.setPhoneVerified(true);
        memberRepository.save(member);
    }

    public void updateMemberRole(String mid){
        Member member = memberRepository.findByMid(mid);
        if(member != null){
            member.setRole(Role.MEMBER);
            memberRepository.save(member);
        }
    }

}