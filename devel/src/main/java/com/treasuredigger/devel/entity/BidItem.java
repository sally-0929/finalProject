package com.treasuredigger.devel.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.treasuredigger.devel.constant.ItemStatus;
import com.treasuredigger.devel.dto.BidItemFormDto;
import com.treasuredigger.devel.dto.ItemFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "bid_items_tbl")
@Getter
@Setter
@ToString
public class BidItem extends BaseEntity{

    @Id
    @Column(name = "bid_item_id")
    private String bidItemId;

    @Column(nullable = false)
    private String bidItemName;

    @Column(nullable = false)
    private String bidItemDesc;

    @Column(nullable = false)
    private long startPrice;

    @Column(nullable = false)
    private long maxPrice;

    private LocalDateTime bidStartDate;

    private LocalDateTime bidEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus itemStatus;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;



    @ManyToOne
    @JoinColumn(name = "cid", referencedColumnName = "cid")
    private ItemCategory itemCategory;

    @OneToMany(mappedBy = "bidItem", cascade = CascadeType.ALL)
    private List<ItemAsk> itemAsks;

    @OneToMany(mappedBy = "bidItem", cascade = CascadeType.ALL)
    private List<Bid> bids;

    public void updateItem(BidItemFormDto itemFormDto){
        this.bidItemName = itemFormDto.getBidItemName();
        this.bidStartDate = itemFormDto.getBidStartDate();
        this.bidEndDate = itemFormDto.getBidEndDate();
        this.bidItemDesc = itemFormDto.getBidItemDesc();

        this.itemStatus = itemFormDto.getItemStatus();

    }

}
