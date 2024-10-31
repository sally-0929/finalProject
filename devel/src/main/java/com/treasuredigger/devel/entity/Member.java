package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.constant.Role;
import com.treasuredigger.devel.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="member")
@Getter @Setter
public class Member extends BaseEntity {

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String mid;

    private String email;

    private String password;

    private String address;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean emailVerified = false;

    private boolean phoneVerified = false;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL) // MemberGrade와의 관계
    private MemberGrade memberGrade;

    public Member() {

    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wishlist> wishlists;


    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setAddress(memberFormDto.getAddress());
        member.setMid(memberFormDto.getMid());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.USER);

        MemberGrade memberGrade = new MemberGrade(member);
        member.setMemberGrade(memberGrade);

        return member;
    }

    public void updateMember(MemberFormDto memberFormDto) {
        this.name = memberFormDto.getName();
        this.email = memberFormDto.getEmail();
        this.phone = memberFormDto.getPhone();
        this.address = memberFormDto.getAddress();
    }

}
