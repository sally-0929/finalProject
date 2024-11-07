package com.treasuredigger.devel.service;

import com.treasuredigger.devel.comm.GeneratedKey;
import com.treasuredigger.devel.constant.ItemStatus;
import com.treasuredigger.devel.dto.*;

import com.treasuredigger.devel.entity.*;
import com.treasuredigger.devel.mapper.BidItemMapper;
import com.treasuredigger.devel.repository.BidItemImgRepository;
import com.treasuredigger.devel.repository.BidItemRepository;
import com.treasuredigger.devel.repository.CategoryRepository;
import com.treasuredigger.devel.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
//@Transactional
@RequiredArgsConstructor
public class BidItemService {

    private final BidItemRepository bidItemRepository;
    private final BidItemImgService itemImgService;
    private final BidItemImgRepository itemImgRepository;
    private final CategoryRepository itemCategoryRepository;
    private final MemberRepository memberRepository;
    private final BidService bidService;

    @Autowired
    private GeneratedKey generatedKey;

    @Autowired
    private BidItemMapper bidItemMapper;


//    @Transactional
    public void updateItemStatuses() {
        System.out.print("updateStatus 메서드 실행");
        LocalDateTime now = LocalDateTime.now();
        List<BidItem> bidItems = bidItemRepository.findAll();

        for (BidItem bidItem : bidItems) {
            if (bidItem.getBidStartDate().isBefore(now) && bidItem.getBidEndDate().isAfter(now)) {
                bidItem.setItemStatus(ItemStatus.ING);
            } else if (bidItem.getBidEndDate().isBefore(now)) {
                bidItem.setItemStatus(ItemStatus.END);
            } else if (bidItem.getBidStartDate().isAfter(now)) {
                bidItem.setItemStatus(ItemStatus.WAIT);
            }
            bidItemRepository.save(bidItem);
        }
    }




        public Page<BidItemDto> getList(String searchQuery,Pageable pageable, String cid, String auctionStatus){
        List<BidItemDto> items = bidItemMapper.selectBidList(searchQuery,pageable, cid, auctionStatus);
        int total = bidItemMapper.countBidItems(searchQuery, cid, auctionStatus);
        return new PageImpl<>(items, pageable, total);
        //return bidItemMapper.selectBidList();
    }


//    @Transactional
    public void saveItem(BidItemFormDto bidItemFormDto, List<MultipartFile> itemImgFileList,String mid) throws Exception {

        ItemCategory itemCategory = itemCategoryRepository.findById(bidItemFormDto.getCid()).orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByMid(mid);

        BidItem bidItem = bidItemFormDto.createBidItem(member, itemCategory);
        bidItem.setBidItemId(generatedKey.itemKey(bidItemFormDto.getCid()));
        bidItemRepository.save(bidItem);


        for(int i=0;i<itemImgFileList.size();i++){
            BidItemImg bidItemImg = new BidItemImg();
            bidItemImg.setBidItem(bidItem);

            if(i == 0)
                bidItemImg.setBidRepimgYn("Y");
            else
                bidItemImg.setBidRepimgYn("N");

            System.out.println("bidItemImg data +++++++++++++" +  bidItemImg);
            itemImgService.saveItemImg(bidItemImg, itemImgFileList.get(i), true);
        }
        // bid table에도 저장하게끔

        BidDto bidDto = new BidDto();

        bidService.saveBid(bidItem.getBidItemId(), member.getId(), bidItemFormDto.getStartPrice(), "N");
    }

    @Transactional(readOnly = true)
    public BidItemFormDto getItemDtl(String itemId) {
        List<BidItemImg> itemImgList = itemImgRepository.findByBidItem_BidItemIdOrderByIdAsc(itemId);
        List<BIdItemImgDto> itemImgDtoList = new ArrayList<>();
        for (BidItemImg itemImg : itemImgList) {
            BIdItemImgDto itemImgDto = BIdItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        BidItem item = bidItemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        BidItemFormDto itemFormDto = BidItemFormDto.of(item);
        itemFormDto.setBiditemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public BidItemDto viewDtl(String bidItemId) {
        List<BidItemImg> itemImgList = itemImgRepository.findByBidItem_BidItemIdOrderByIdAsc(bidItemId);
        List<BIdItemImgDto> itemImgDtoList = new ArrayList<>();
        for (BidItemImg itemImg : itemImgList) {
            BIdItemImgDto itemImgDto = BIdItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        BidItemDto bidItemDto = bidItemMapper.selectBidItemById(bidItemId);
        bidItemDto.setBiditemImgDtoList(itemImgDtoList);

        System.out.println("dto value " + bidItemDto);
        return bidItemDto;
    }


    public void deleteBidItem(String bidItemId) {
        bidItemRepository.deleteById(bidItemId);
    }

//    public void updateItem(BidItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
//        BidItem item = bidItemRepository.findById(itemFormDto.getBidItemId()).orElseThrow(EntityNotFoundException::new);
//
//
//        ItemCategory itemCategory = itemCategoryRepository.findById(itemFormDto.getCid()).orElseThrow(EntityNotFoundException::new);
//
//        item.updateItem(itemFormDto,itemCategory);
//        List<Long> itemImgIds = itemFormDto.getBiditemImgIds();
//
//        for (int i = 0; i < itemImgFileList.size(); i++) {
//            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i), true);
//        }
//    }

}
