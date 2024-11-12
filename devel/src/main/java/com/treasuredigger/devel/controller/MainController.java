package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.WishlistDto;
import com.treasuredigger.devel.service.CategoryService;
import com.treasuredigger.devel.service.ItemService;
import com.treasuredigger.devel.service.WishlistService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.treasuredigger.devel.dto.ItemSearchDto;
import com.treasuredigger.devel.dto.MainItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final WishlistService wishlistService;

    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, @RequestParam("size") Optional<Integer> size, Optional<String> cid, Model model, Authentication authentication) {
        int pageSize = size.orElse(20);
        Pageable pageable = PageRequest.of(page.orElse(0), pageSize);
        Page<MainItemDto> items;
        if (cid.isPresent()) {
            items = itemService.getMainItemPageByCategory(cid.get(), itemSearchDto, pageable); // 카테고리별 항목 조회
        } else {
            items = itemService.getMainItemPage(itemSearchDto, pageable); // 전체 항목 조회
        }

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);

        // 각 아이템의 판매 상태를 추가
        for (MainItemDto item : items) {
            item.setItemSellStatus(itemService.getItemSellStatus(item.getId())); // 판매 상태 추가
        }
        model.addAttribute("categories", categoryService.list());

        if (authentication != null && authentication.isAuthenticated()) {
            String userId = authentication.getName();
            List<Long> wishlistItemIds = wishlistService.getWishlistItemIdsByMember(userId); // 찜한 아이템 ID 목록 가져오기
            items.forEach(item -> item.setWishlisted(wishlistItemIds.contains(item.getId()))); // 각 아이템의 isWishlisted 설정
            List<WishlistDto> recentItemWishlist = wishlistService.getRecentItemWishlistByMember(userId, 3);
            model.addAttribute("recentItemWishlist", recentItemWishlist);
        }

        return "main";
    }
    @GetMapping(value = "/loadItems")
    public String loadItems(@RequestParam("page") int page,
                            @RequestParam("size") Optional<Integer> size,
                            @RequestParam("cid") Optional<String> cid,
                            @RequestParam("searchQuery") Optional<String> searchQuery,
                            ItemSearchDto itemSearchDto, Model model) {
        int pageSize = size.orElse(20);
        Pageable pageable = PageRequest.of(page, pageSize);
        itemSearchDto.setSearchQuery(searchQuery.orElse(""));
        Page<MainItemDto> items;
        if (cid.isPresent()) {
            items = itemService.getMainItemPageByCategory(cid.get(), itemSearchDto, pageable);
        } else {
            items = itemService.getMainItemPage(itemSearchDto, pageable);
        }
        items.forEach(item -> item.setItemSellStatus(itemService.getItemSellStatus(item.getId())));
        model.addAttribute("items", items);

        return "main :: #item-container"; // 추가 항목을 렌더링할 부분 뷰 리턴
    }

}