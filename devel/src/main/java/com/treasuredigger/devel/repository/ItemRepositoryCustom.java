package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.dto.ItemSearchDto;
import com.treasuredigger.devel.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.treasuredigger.devel.dto.MainItemDto;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPageByCategory(String cid, ItemSearchDto itemSearchDto, Pageable pageable);
}