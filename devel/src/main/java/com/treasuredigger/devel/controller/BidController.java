package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.*;
import com.treasuredigger.devel.service.BidItemService;
import com.treasuredigger.devel.service.CategoryService;
import com.treasuredigger.devel.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/biditem")
@Log4j2
public class BidController {

    private final CategoryService categoryService;

    private final BidItemService bidItemService;

    @GetMapping("/list")
    public void bidlist(Model model){
            log.info("model value ++ " + bidItemService.getList());
           model.addAttribute("bidItemList", bidItemService.getList());


    }

    @GetMapping("/register")
    public String bidregister(Model model) {
        model.addAttribute("bidItemFormDto", new BidItemFormDto());
        model.addAttribute("categories", categoryService.list());
        log.info("category" + categoryService.list());

        return "biditem/register";
    }

    @PostMapping(value = "/register")
    public String itemNew(@Valid BidItemFormDto BiditemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        log.info("itemImgFileList + " + itemImgFileList);
        model.addAttribute("categories", categoryService.list());

        if(bindingResult.hasErrors()){
            return "biditem/register";
        }

        if(itemImgFileList.get(0).isEmpty() && BiditemFormDto.getBidItemId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "biditem/register";
        }

        try {
            bidItemService.saveItem(BiditemFormDto, itemImgFileList);
        } catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "biditem/register";
        }

        return "redirect:/biditem/list";
    }
}
