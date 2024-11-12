package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.WishlistDto;
import com.treasuredigger.devel.dto.ItemDto;
import com.treasuredigger.devel.dto.OrderHistDto;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.entity.ItemCategory;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.service.CategoryService;
import com.treasuredigger.devel.service.ItemService;
import com.treasuredigger.devel.service.MemberGradeService;
import com.treasuredigger.devel.service.MemberService;
import com.treasuredigger.devel.service.WishlistService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;
import com.treasuredigger.devel.dto.ItemFormDto;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import com.treasuredigger.devel.dto.ItemSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final WishlistService wishlistService;

    private final MemberService memberService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        model.addAttribute("categories", categoryService.list());
        return "item/itemForm";
    }


    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){
            model.addAttribute("categories", categoryService.list());
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            model.addAttribute("categories", categoryService.list());
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            model.addAttribute("categories", categoryService.list());
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
            model.addAttribute("categories", categoryService.list());
        } catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            model.addAttribute("categories", categoryService.list());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("categories", categoryService.list());
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            model.addAttribute("categories", categoryService.list());
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            model.addAttribute("categories", categoryService.list());
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(@ModelAttribute("itemSearchDto") ItemSearchDto itemSearchDto,
                             @PathVariable("page") Optional<Integer> page, Model model) {

        Pageable pageable = PageRequest.of(page.orElse(0), 10);
        Page<ItemDto> items = itemService.getAdminItemDtos(pageable, itemSearchDto); // 검색 조건 추가
        model.addAttribute("items", items);
        model.addAttribute("maxPage", 5);
        model.addAttribute("categories", categoryService.list());
        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId, Authentication authentication){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        model.addAttribute("itemSellStatus", itemService.getItemSellStatus(itemId)); // 판매 상태 추가
        if (authentication != null && authentication.isAuthenticated()) {
            String userId = authentication.getName();
            List<WishlistDto> recentItemWishlist = wishlistService.getRecentItemWishlistByMember(userId, 3);
            model.addAttribute("recentItemWishlist", recentItemWishlist);
        }
        return "item/itemDtl";
    }

    @GetMapping(value = {"/sale", "/sale/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model) {
        System.out.println("prin value" + principal.getName());
        Pageable pageable = PageRequest.of(page.orElse(0), 4);
        String email = principal.getName();
        Member member = memberService.findMemberByMid(email);
        List<ItemFormDto> saleHistDtoList = itemService.getItemsBySeller(member, pageable);
        System.out.println("saleHistList" + saleHistDtoList);
        model.addAttribute("items", saleHistDtoList);

        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "item/saleHist";
    }
}