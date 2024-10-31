package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.dto.BidItemFormDto;
import com.treasuredigger.devel.dto.WishDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "wishlist_tbl")
@Getter
@Setter
public class Wishlist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "bid_item_id", referencedColumnName = "bid_item_id")
    private BidItem bidItem;


    public static Wishlist fromDto(WishDto wishDto, Member member, Item item, BidItem bidItem) {
        Wishlist wishlist = new Wishlist();
        wishlist.setMember(member);
        wishlist.setItem(item);
        wishlist.setBidItem(bidItem);
        return wishlist;
    }

}
