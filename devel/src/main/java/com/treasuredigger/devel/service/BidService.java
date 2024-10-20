package com.treasuredigger.devel.service;


import com.treasuredigger.devel.dto.BidItemFormDto;
import com.treasuredigger.devel.dto.ItemFormDto;
import com.treasuredigger.devel.entity.BidItem;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.entity.ItemImg;
import com.treasuredigger.devel.repository.BidItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BidService {

    final private BidItemRepository BidItemRepository;

    public void registerBidItem(BidItemFormDto biditemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //상품 등록
        BidItem biditem = biditemFormDto.createBidItem();
        BidItemRepository.save(biditem);

//        //이미지 등록
//        for(int i=0;i<itemImgFileList.size();i++){
//            ItemImg itemImg = new ItemImg();
//            itemImg.setItem(item);
//
//            if(i == 0)
//                itemImg.setRepimgYn("Y");
//            else
//                itemImg.setRepimgYn("N");
//
//            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
//        }


    }
}
