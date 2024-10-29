package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.*;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/biditem")
@Log4j2
public class BidController {

    private final CategoryService categoryService;

    private final BidItemService bidItemService;

    private final BidService bidService;

    private final MemberService memberService;

    private final MemberGradeService memberGradeService;

    @GetMapping("/list")
    public void bidlist(Model model, @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                        @RequestParam(value = "searchQuery", required = false) String searchQuery,
                        @RequestParam(value = "cid", required = false) String cid){
            if(page <0 ) page = 0;

            bidItemService.updateItemStatuses();
            Pageable pageable = PageRequest.of(page, 15);
            Page<BidItemDto> bidItemPage = bidItemService.getList(searchQuery,pageable, cid);
            log.info("model value ++ " + bidItemPage);

            model.addAttribute("categories", categoryService.list());
           model.addAttribute("bidItemList", bidItemPage.getContent());
           model.addAttribute("currentPage", bidItemPage.getNumber());
           model.addAttribute("totalPages", bidItemPage.getTotalPages());

           log.info("category List Page data +++" + categoryService.list());

           log.info("현재페이지 로그" + bidItemPage.getNumber());
           log.info("토탈 로그" + bidItemPage.getTotalPages());


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
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,Principal principal){

        String email = principal.getName();

        System.out.println("email " + email);
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
            bidItemService.saveItem(BiditemFormDto, itemImgFileList, email);
            Member member = memberService.findMemberByMid(email);
            memberGradeService.incrementMgdesc(member);
        } catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "biditem/register";
        }

        return "redirect:/biditem/list";
    }

    @GetMapping(value = "/view/{bidItemId}")
    public String itemDtl(Model model, @PathVariable("bidItemId") String bidItemId){
        BidItemDto bidItemDto =  bidItemService.viewDtl(bidItemId);
        model.addAttribute("biditem", bidItemDto);
        model.addAttribute("minPrice", bidItemDto.getBidNowPrice() + 1);
        log.info("minPrice " + bidItemDto.getBidNowPrice() + 1);
        return "biditem/view";
    }

    @PostMapping("/placeBid")
    public ResponseEntity<String> placeBid(@RequestParam("bidNowPrice") int bidNowPrice, @RequestParam("bidItemId") String bidItemId, Principal principal) {

        Member member =  memberService.findMemberByMid(principal.getName());
        Long mid = member.getId();
        log.info("mid " + mid);
        bidService.saveBid(bidItemId,mid,bidNowPrice);

        return ResponseEntity.ok("Success");
    }


    @GetMapping("/getBidList")
    public @ResponseBody List<BidDto> getBidList(@RequestParam("bidItemId") String bidItemId) {
        log.info("getBidList 메서드 호출됨. bidItemId: " + bidItemId);
        List<BidDto> bidList = bidService.getBidList(bidItemId);
        log.info("bidList 결과값 : {}", bidList);
        return bidList;
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelBidItem(@RequestParam("bidItemId") String bidItemId) {
        log.info("biditem value +++++" + bidItemId);
        bidItemService.deleteBidItem(bidItemId);
        return ResponseEntity.ok("Auction canceled");
    }




}
