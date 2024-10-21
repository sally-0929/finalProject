package com.treasuredigger.devel.service;

import com.treasuredigger.devel.comm.GeneratedKey;
import com.treasuredigger.devel.dto.*;

import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.BidItemImg;
import com.treasuredigger.devel.entity.ItemCategory;
import com.treasuredigger.devel.entity.ItemImg;
import com.treasuredigger.devel.mapper.BidItemMapper;
import com.treasuredigger.devel.repository.BidItemImgRepository;
import com.treasuredigger.devel.repository.BidItemRepository;
import com.treasuredigger.devel.repository.CategoryRepository;
import com.treasuredigger.devel.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private GeneratedKey generatedKey;

    @Autowired
    private BidItemMapper bidItemMapper;

    public List<BidItemDto> getList(){
        return bidItemMapper.selectBidList();
    }


//    @Transactional
    public void saveItem(BidItemFormDto bidItemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        ItemCategory itemCategory = itemCategoryRepository.findById(bidItemFormDto.getCid()).orElseThrow(EntityNotFoundException::new);

        BidItem bidItem = bidItemFormDto.createBidItem(itemCategory);
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
