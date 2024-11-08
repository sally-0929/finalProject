package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.*;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
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

    private final WishlistService wishlistService;

    private final MemberGradeService memberGradeService;

    private final OrderService orderService;

    @Qualifier("taskScheduler")
    private final TaskScheduler taskScheduler;

    @GetMapping("/list")
    public void bidlist(Model model, @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                        @RequestParam(value = "searchQuery", required = false) String searchQuery,
                        @RequestParam(value = "cid", required = false) String cid, @RequestParam(value ="auctionStatus" , required = false) String auctionStatus, Authentication authentication){
            if(page <0 ) page = 0;
            log.info("auctionStatus ++ " + auctionStatus);
            bidItemService.updateItemStatuses();
            Pageable pageable = PageRequest.of(page, 15);
            Page<BidItemDto> bidItemPage = bidItemService.getList(searchQuery,pageable, cid, auctionStatus);
            log.info("model value ++ " + bidItemPage);

            model.addAttribute("categories", categoryService.list());
            model.addAttribute("bidItemList", bidItemPage.getContent());
            model.addAttribute("currentPage", bidItemPage.getNumber());
            model.addAttribute("totalPages", bidItemPage.getTotalPages());
            model.addAttribute("auctionStatus", auctionStatus);

            if (authentication != null && authentication.isAuthenticated()) {
                String userId = authentication.getName();
                List<WishlistDto> recentBidWishlist = wishlistService.getRecentBidWishlistByMember(userId, 3);
                model.addAttribute("recentBidWishlist", recentBidWishlist);
            }
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
            LocalDateTime bidEndDate = BiditemFormDto.getBidEndDate();
            Runnable task = runTask();
            taskScheduler.schedule(task, Instant.from(bidEndDate));

        } catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "biditem/register";
        }

        return "redirect:/biditem/list";
    }

    @GetMapping(value = "/view/{bidItemId}")
    public String itemDtl(Model model, @PathVariable("bidItemId") String bidItemId, Authentication authentication){
        BidItemDto bidItemDto =  bidItemService.viewDtl(bidItemId);
        bidItemService.updateItemStatuses();
        model.addAttribute("biditem", bidItemDto);
        model.addAttribute("minPrice", bidItemDto.getBidNowPrice() + 1);
        if (authentication != null && authentication.isAuthenticated()) {
            String userId = authentication.getName();
            List<WishlistDto> recentBidWishlist = wishlistService.getRecentBidWishlistByMember(userId, 3);
            model.addAttribute("recentBidWishlist", recentBidWishlist);
        }
        return "biditem/view";
    }

    @PostMapping("/placeBid")
    public ResponseEntity<?> placeBid(@RequestParam("bidNowPrice") int bidNowPrice, @RequestParam("bidItemId") String bidItemId,
                                           @RequestParam("buyNowCheck") String buyNowCheck,Principal principal) {
        System.out.println("buyNowCheck + " + buyNowCheck + bidNowPrice);
        Member member =  memberService.findMemberByMid(principal.getName());


        Long mid = member.getId();
        log.info("mid " + mid);


        //결재가 완료 되어야 그 후에 즉시구매한 값으로 수정 (선수작업 결재)
        if (buyNowCheck != null && buyNowCheck.trim().equals("N")) {
            bidService.saveBid(bidItemId, mid, bidNowPrice, buyNowCheck);
            return  new ResponseEntity<String>("Bid placed successfully", HttpStatus.OK);
        }else {
            Long orderId =  orderService.orderBidItem(bidItemId, principal.getName());
            return new ResponseEntity<Long>(orderId, HttpStatus.OK);
        }



    }


    @GetMapping("/getBidList")
    public @ResponseBody List<BidDto> getBidList(@RequestParam("bidItemId") String bidItemId) {
        log.info("getBidList 메서드 호출됨. bidItemId: " + bidItemId);
        List<BidDto> bidList = bidService.getBidList(bidItemId);
        log.info("bidList 결과값 : {}", bidList);
        return bidList;
    }

    @GetMapping("/edit/{bidItemId}")
    public String editBidItemForm(@PathVariable String bidItemId, Model model) {
        log.info("edit 컨트롤러 실행");
        try {
            model.addAttribute("bidItemFormDto", new BidItemFormDto());
            BidItemDto bidItemDto = bidItemService.viewDtl(bidItemId);
            model.addAttribute("categories", categoryService.list());
            model.addAttribute("biditem", bidItemDto);
            return "biditem/edit";
        } catch (Exception e) {
            e.printStackTrace(); // 예외 출력
            return "error"; // 오류 페이지로 리다이렉트
        }
    }

//    @PostMapping("/biditem/edit")
//    public String editBidItem(@RequestParam String bidItemId, @RequestParam String name,
//                              @RequestParam String description, @RequestParam("image") MultipartFile image,
//                              @RequestParam String endDate) {
//        bidItemService.updateBidItem(bidItemId, name, description, image, endDate);
//        return "redirect:/biditem/view/" + bidItemId;
//    }


    @PostMapping("/cancel")
    public ResponseEntity<String> cancelBidItem(@RequestParam("bidItemId") String bidItemId) {
        log.info("biditem value +++++" + bidItemId);
        bidItemService.deleteBidItem(bidItemId);
        return ResponseEntity.ok("Auction canceled");
    }

    @GetMapping("/mybidlist")
    public void mybidList(Principal principal, Model model){

        List<BidItemDto> mylist =  bidService.getBidMyList(principal.getName());
        System.out.println("mybidList" + mylist);
        model.addAttribute("mybidList" , mylist);

    }

    private Runnable runTask() {
        return () -> {
            log.info("낙찰된 사람한테 메일 발송하는 로직 ");
        };
    }




}
