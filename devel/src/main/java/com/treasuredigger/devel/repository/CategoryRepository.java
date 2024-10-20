package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<ItemCategory, String> {

}
