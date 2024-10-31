package com.treasuredigger.devel.dto;

import com.treasuredigger.devel.entity.Member;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionMember implements Serializable {
    private String name;
    private String email;
    private String roleKey;

    public SessionMember(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.roleKey = member.getRoleKey();
    }
}
