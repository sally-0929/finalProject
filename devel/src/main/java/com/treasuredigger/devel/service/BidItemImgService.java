package com.treasuredigger.devel.service;

import com.treasuredigger.devel.entity.BidItemImg;
import com.treasuredigger.devel.entity.ItemImg;
import com.treasuredigger.devel.repository.BidItemImgRepository;
import com.treasuredigger.devel.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
//@Transactional
public class BidItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    @Value("${auctionImgLocation}")
    private String auctionImgLocation;

    private final BidItemImgRepository itemImgRepository;
    private final FileService fileService;

    public void saveItemImg(BidItemImg itemImg, MultipartFile itemImgFile, boolean isAuction) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";
        String targetLocation = isAuction ? auctionImgLocation : itemImgLocation;

        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(targetLocation, oriImgName, itemImgFile.getBytes());
            imgUrl = (isAuction ? "/images/auction/" : "/images/item/") + imgName;
            System.out.println("File uploaded successfully: " + imgName);
        }

        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        System.out.println("Updated Item Image: oriImgName=" + oriImgName + ", imgName=" + imgName + ", imgUrl=" + imgUrl);

        itemImgRepository.save(itemImg);
        System.out.println("Item image saved: " + itemImg);
    }


    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile, boolean isAuction) throws Exception {
        if (!itemImgFile.isEmpty()) {
            BidItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);
            String targetLocation = isAuction ? auctionImgLocation : itemImgLocation;

            // 기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedItemImg.getBidImgName())) {
                fileService.deleteFile(targetLocation + "/" + savedItemImg.getBidImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(targetLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = (isAuction ? "/images/auction/" : "/images/item/") + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }
}
