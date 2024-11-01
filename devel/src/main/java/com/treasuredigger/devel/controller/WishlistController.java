package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.BidItemDto;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.service.BidItemService;
import com.treasuredigger.devel.service.MemberService;
import com.treasuredigger.devel.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BidItemService bidItemService;

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkWishlist(@RequestParam("bidItemId") String bidItemId, Principal principal) {
        String email  = principal.getName();
        Member member = memberService.findMemberByMid(email);
        BidItemDto bidItemDto =  bidItemService.viewDtl(bidItemId);

        BidItem bidItem = bidItemDto.convertDtoToEntity(bidItemDto);
        System.out.print("email data + "+  email +"bidItem Id " + bidItemId + "member " + member + "bidItemDto" + bidItemDto);

        boolean isWishlisted = wishlistService.isWishlisted(bidItem, member);
        return ResponseEntity.ok(isWishlisted);
    }

    @PostMapping("/toggle")
    public ResponseEntity<String> toggleWishlist(@RequestParam("bidItemId") String bidItemId, Principal principal) {
        String email  = principal.getName();
        Member member = memberService.findMemberByMid(email);
        BidItemDto bidItemDto =  bidItemService.viewDtl(bidItemId);
        BidItem bidItem = bidItemDto.convertDtoToEntity(bidItemDto);
        wishlistService.toggleWishlist(bidItem, member);
        return ResponseEntity.ok("Success");
    }
}
