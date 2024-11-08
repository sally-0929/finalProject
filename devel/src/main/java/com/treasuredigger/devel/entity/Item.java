package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import com.treasuredigger.devel.dto.ItemFormDto;
import com.treasuredigger.devel.exception.OutOfStockException;

import java.util.List;

@Entity
@Table(name="item")
@Getter
@Setter
public class Item extends BaseEntity {

    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;       //상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(name="price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    @ManyToOne
    @JoinColumn(name = "cid", referencedColumnName = "cid")
    private ItemCategory itemCategory;

    @ManyToOne
    @JoinColumn(name = "member_id") // 판매자 ID 컬럼
    private Member seller;
    // ItemImg와의 관계 설정
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImg> itemImgs;

    // Wishlist와의 관계 설정
     @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
     private List<Wishlist> wishlists;

    // CartItem와의 관계 설정
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    // OrderItem와의 관계 설정
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();

        if (this.stockNumber > 0) {
            this.itemSellStatus = ItemSellStatus.SELL;
        } else {
            this.itemSellStatus = ItemSellStatus.SOLD_OUT;
        }
    }

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
        // 재고가 0 이하가 되면 품절로 상태 변경
        if (this.stockNumber == 0) {
            this.itemSellStatus = ItemSellStatus.SOLD_OUT;
        }
    }

    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
        if (this.stockNumber > 0) {
            this.itemSellStatus = ItemSellStatus.SELL;
        }
    }

}