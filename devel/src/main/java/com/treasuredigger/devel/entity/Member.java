package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.constant.Role;
import com.treasuredigger.devel.dto.MemberFormDto;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="member")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
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

    private String provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean emailVerified = false;

    private boolean phoneVerified = false;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL) // MemberGrade와의 관계
    private MemberGrade memberGrade;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL) // Bid와의 관계
    private List<Bid> bids;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL) // BidItem와의 관계
    private List<BidItem> bidItems;

    @OneToMany(mappedBy = "member") // order와의 관계
    private List<Order> orders;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL) // inquirys와의 관계
    private List<Inquiry> inquirys;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL) // wishlists와의 관계
    private List<Wishlist> wishlists;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL) // items와의 관계
    private List<Item> items;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL) // carts와의 관계
    private List<Cart> carts;

    // 추가: 포인트 필드
    private int points = 0;  // 기본값은 0

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setAddress(memberFormDto.getAddress());
        member.setMid(memberFormDto.getMid());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.USER);

        // 기본 등급 생성
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

    // 포인트 증가
    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }

    // 포인트 차감
    public void deductPoints(int pointsToDeduct) {
        this.points = Math.max(0, this.points - pointsToDeduct);  // 포인트가 0 이하로 떨어지지 않도록
    }

    @Builder
    public Member(Long id, String mid, String name, String email, String password, Role role, String provider, MemberGrade memberGrade) {
        this.id = id;
        this.mid = mid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.memberGrade = memberGrade;
    }

    public Member update(String name) {
        this.name = name;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

}
