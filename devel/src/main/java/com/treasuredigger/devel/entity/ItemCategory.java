package com.treasuredigger.devel.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "category_tbl")
@Getter
@Setter
public class ItemCategory {

    @Id
    private String cid;

    private String cname;

    private String cdesc;
}
