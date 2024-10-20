package com.treasuredigger.devel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="bid_item_img")
@Getter @Setter
@ToString
public class BidItemImg extends BaseEntity{

    @Id
    @Column(name="bid_item_img_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bidImgName; //이미지 파일명

    private String bidOriImgName; //원본 이미지 파일명

    private String bidImgUrl; //이미지 조회 경로

    private String bidRepimgYn; //대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid_item_id")
    private BidItem bidItem;

    public void updateItemImg(String bidOriImgName, String bidImgName, String bidImgUrl) {
        this.bidOriImgName = bidOriImgName;
        this.bidImgName = bidImgName;
        this.bidImgUrl = bidImgUrl;
    }


}