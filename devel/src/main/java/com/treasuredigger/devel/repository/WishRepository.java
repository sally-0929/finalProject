package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository  extends JpaRepository<Wishlist, Long> {
}
