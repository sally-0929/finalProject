package com.treasuredigger.devel.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bids_tbl")
@Getter @Setter
public class Bid extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bino;

    @Column(nullable = false)
    private long bidRegPrice;

    private LocalDateTime bidRegTime;

    private String buyNowCheck;

    @ManyToOne
    @JoinColumn(name = "bid_item_id", referencedColumnName = "bid_item_id")
    private BidItem bidItem;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    public String getBidItemId() { 
        return this.bidItem != null ? this.bidItem.getBidItemId() : null; 
    }
}
