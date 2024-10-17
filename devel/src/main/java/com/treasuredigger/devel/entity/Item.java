package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.constant.ItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;


@Entity
@Table(name="items_tbl")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{

    @Id
    @Column(name="item_id",length = 50)
    private String itemId;

    @Column(name="item_name",length = 50, nullable = false)
    private String itemName;

    @Column(name = "item_desc", length = 1000, nullable = false)
    private String itemDesc;

    @Column(name = "start_price")
    private Long startPrice;

    @Column(name = "max_price")
    private Long maxPrice;

    @Column(name = "bid_start_date")
    private Timestamp bidStartDate;

    @Column(name = "bid_end_date")
    private Timestamp bidEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus itemStatus;

    @Column(nullable = false)
    private String cid;

    private String id;


    // 연관관계 매핑
    @ManyToOne
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "cid", referencedColumnName = "cid", insertable = false, updatable = false)
    private ItemCategory itemCategory;



}
