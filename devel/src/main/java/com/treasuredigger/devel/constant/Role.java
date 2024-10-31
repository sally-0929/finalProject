package com.treasuredigger.devel.constant;

public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    MEMBER("ROLE_MEMBER");

    private String key;

    Role(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}