package com.treasuredigger.devel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class RefundReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String merchantUid;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 기본 생성자
    public RefundReason() {}

    // 생성자
    public RefundReason(String merchantUid, String reason) {
        this.merchantUid = merchantUid;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchantUid() {
        return merchantUid;
    }

    public void setMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

