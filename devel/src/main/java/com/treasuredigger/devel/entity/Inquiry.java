package com.treasuredigger.devel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "inquiry")
@Getter @Setter
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 외래 키
    private Member member; // 작성자

    private String title; // 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 내용

    private LocalDateTime createdDate; // 작성일
    private LocalDateTime updatedDate; // 수정일
    private Boolean answered; // 답변 상태
    private LocalDateTime respondedDate; // 답변일
    private String responseContent;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
        this.answered = false; // 기본값은 답변 미완료
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    public void setResponse(String responseContent) {
        this.responseContent = responseContent;
        this.respondedDate = LocalDateTime.now();
        this.answered = true;
    }

    public Date getCreatedDateAsDate() {
        return Timestamp.valueOf(createdDate);
    }

    public Date getRespondedDateAsDate() {
        if (respondedDate != null) {
            return Timestamp.valueOf(respondedDate);
        }
        return null;
    }
}
