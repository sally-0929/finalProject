package com.treasuredigger.devel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_category_tbl")
public class ItemCategory {

    @Id
    private String cid;


    @Column(nullable = false)
    private String cname;

    @Column(nullable = false)
    private String cDesc;
}
