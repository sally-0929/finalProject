package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item,String> {

    long countByItemIdStartingWith(String prefix);
}
