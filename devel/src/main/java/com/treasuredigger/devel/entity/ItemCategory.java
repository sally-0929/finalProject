package com.treasuredigger.devel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="item_category_tbl")
@Getter
@Setter
@ToString
public class ItemCategory extends BaseEntity{
    @Id
    @Column(name="cid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
}
