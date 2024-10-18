package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.constant.AskReplyInform;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "item_ask_tbl")
@Getter
@Setter
public class ItemAsk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iano;

    @ManyToOne
    @JoinColumn(name = "bid_item_id", referencedColumnName = "bid_item_id")
    private BidItem bidItem;

    @Column(nullable = false)
    private String askType;

    @Column(nullable = false)
    private String askShow;

    @Column(nullable = false)
    private String askTitle;

    private String mid;

    @Column(nullable = false)
    private String askContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AskReplyInform askReplyInform;

    @ManyToOne
    @JoinColumn(name = "mid", referencedColumnName = "mid", insertable = false, updatable = false)
    private Member member;




}
