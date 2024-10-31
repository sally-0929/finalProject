package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.service.CategoryService;
import com.treasuredigger.devel.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.treasuredigger.devel.dto.ItemSearchDto;
import com.treasuredigger.devel.dto.MainItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;
    private final CategoryService categoryService; // CategoryService 추가

    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Optional<String> cid, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 15);

        Page<MainItemDto> items;
        if (cid.isPresent()) {
            items = itemService.getMainItemPageByCategory(cid.get(), itemSearchDto, pageable); // 카테고리별 항목 조회
        } else {
            items = itemService.getMainItemPage(itemSearchDto, pageable); // 전체 항목 조회
        }

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        // 각 아이템의 판매 상태를 추가
        for (MainItemDto item : items) {
            item.setItemSellStatus(itemService.getItemSellStatus(item.getId())); // 판매 상태 추가
        }
        model.addAttribute("categories", categoryService.list());

        return "main";
    }


}