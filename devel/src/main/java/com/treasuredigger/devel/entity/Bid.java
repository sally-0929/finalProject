package com.treasuredigger.devel.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bids_tbl")
@Getter
@Setter
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bino;

    @Column(nullable = false)
    private long bidRegPrice;

    private LocalDateTime bidRegTime;

    @ManyToOne
    @JoinColumn(name = "bid_item_id", referencedColumnName = "bid_item_id")
    private BidItem bidItem;

    @ManyToOne
    @JoinColumn(name = "mid", referencedColumnName = "mid", insertable = false, updatable = false)
    private Member member;

}
