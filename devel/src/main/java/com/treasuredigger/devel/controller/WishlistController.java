package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.BidItemDto;
import com.treasuredigger.devel.dto.ItemDto;
import com.treasuredigger.devel.dto.ItemFormDto;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.repository.ItemRepository;
import com.treasuredigger.devel.service.BidItemService;
import com.treasuredigger.devel.service.ItemService;
import com.treasuredigger.devel.service.MemberService;
import com.treasuredigger.devel.service.WishlistService;
import jakarta.persistence.EntityNotFoundException;
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
    private ItemService itemService;

    @Autowired
    private BidItemService bidItemService;

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkWishlist(@RequestParam("entityType") String entityType,
                                                 @RequestParam("entityId") String entityId,
                                                 Principal principal) {
        String email = principal.getName();
        Member member = memberService.findMemberByMid(email);

        boolean isWishlisted = false;
        try {
            if ("biditem".equalsIgnoreCase(entityType)) {
                BidItemDto bidItemDto = bidItemService.viewDtl(entityId);
                BidItem bidItem = bidItemDto.convertDtoToEntity(bidItemDto);
                isWishlisted = wishlistService.isWishlisted(bidItem, member);
            } else if ("item".equalsIgnoreCase(entityType)) {
                Long itemId = Long.parseLong(entityId);
                Item item = itemRepository.findById(Long.valueOf(entityId))
                        .orElseThrow(EntityNotFoundException::new);

                System.out.println("check +++ " + item);

                isWishlisted = wishlistService.isWishlisted(item, member);
            } else {
                return ResponseEntity.badRequest().body(false);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(false);
        }

        return ResponseEntity.ok(isWishlisted);
    }

    @PostMapping("/toggle")
    public ResponseEntity<String> toggleWishlist(@RequestParam("entityId") String entityId,
                                                 @RequestParam("entityType") String entityType,
                                                 Principal principal) {
        String email = principal.getName();
        Member member = memberService.findMemberByMid(email);

        try {
            if ("biditem".equalsIgnoreCase(entityType)) {
                BidItemDto bidItemDto = bidItemService.viewDtl(entityId);
                BidItem bidItem = bidItemDto.convertDtoToEntity(bidItemDto);
                wishlistService.toggleWishlist(bidItem, member);
            } else if ("item".equalsIgnoreCase(entityType)) {

                Item item = itemRepository.findById(Long.valueOf(entityId))
                        .orElseThrow(EntityNotFoundException::new);
                wishlistService.toggleWishlist(item, member);
            } else {
                return ResponseEntity.badRequest().body("Invalid entityType");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid entityId format");
        }

        return ResponseEntity.ok("Success");
    }
}
