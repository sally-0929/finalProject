package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.dto.WishDto;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.service.MemberService;
import com.treasuredigger.devel.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;
    private final MemberService memberService;


    @PostMapping("/add")
    public String addToWishlist(
                                @RequestParam("itemId") Long itemId,
                                @RequestParam("bidItemId") String bidItemId,
                                Model model ,Principal principal) {
        String email = principal.getName();
        WishDto wishDto = new WishDto();
        Member member = memberService.findMemberByMid(email);

        wishDto.setMemberId(member.getId());
        wishDto.setItemId(itemId);
        wishDto.setBidItemId(bidItemId);
        System.out.println("wishDto " + wishDto);

        wishService.saveWish(wishDto);

        model.addAttribute("message", "상품이 찜 목록에 추가되었습니다.");
        return "redirect:/";
    }

//    @GetMapping("/list")
//    public ResponseEntity<List<WishDto>> getWishlist(Principal principal) {
//        Long memberId = getMemberId(principal);
//        List<WishDto> wishlist = wishService.getWishlistForMember(memberId);
//        return ResponseEntity.ok(wishlist);
//    }

//    @DeleteMapping("/remove/{id}")
//    public ResponseEntity<String> removeWish(@PathVariable Long id, Principal principal) {
//        Long memberId = getMemberId(principal);
//        wishService.removeWish(id, memberId);
//        return ResponseEntity.ok("찜 삭제 완료");
//    }

    private Long getMemberId(Principal principal) {
        // 실제 구현에서 principal을 사용하여 Member ID를 가져오는 로직 추가
        // 예: return memberRepository.findByEmail(principal.getName()).getId();
        return 1L; // 예시로 임의의 값 반환
    }
}
