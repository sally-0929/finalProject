package com.treasuredigger.devel.service;

import com.treasuredigger.devel.comm.GeneratedKey;
import com.treasuredigger.devel.constant.ItemStatus;
import com.treasuredigger.devel.dto.*;

import com.treasuredigger.devel.entity.*;
import com.treasuredigger.devel.mapper.BidItemMapper;
import com.treasuredigger.devel.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
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

    @Qualifier("taskScheduler")
    private final TaskScheduler taskScheduler;

    @Autowired
    private GeneratedKey generatedKey;

    @Autowired
    private BidItemMapper bidItemMapper;
    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderService  orderService;

//    @Transactional
    public void updateItemStatuses() {

        // 시간에 따라 update
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

        //즉시 구매 이벤트 발생했을 경우 업데이트
        List<Bid> bids = bidRepository.findAll();
        for (Bid bid : bids) {
            if ("Y".equals(bid.getBuyNowCheck())) {
                BidItem bidItem = bidItemRepository.findByBidItemId(bid.getBidItemId());
                bidItem.setItemStatus(ItemStatus.END);
                bidItemRepository.save(bidItem);
            }
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

        LocalDateTime bidEndDate = bidItemFormDto.getBidEndDate();
        // LocalDateTime을 ZonedDateTime으로 변환 후 Instant로 변환
        ZonedDateTime zonedDateTime = bidEndDate.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Runnable task = runTask(bidItem.getBidItemId());
        taskScheduler.schedule(task, Date.from(instant));


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

    private Runnable runTask(String bidItemId) {
        return () -> {
            BidDto bidDto =  bidItemMapper.getSuccessfulBid(bidItemId);
            //order로 옮겨주는 로직 필요
            System.out.println("task log " + bidDto.toString());
            Long orderId =  orderService.orderBidItem(bidItemId, bidDto.getMid(), bidDto.getBidRegPrice());


            String htmlContent = "<html>"
                    + "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif;\">"
                    + "<table align=\"center\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #f3f4f6; padding: 40px 0;\">"
                    + "  <tr>"
                    + "    <td>"
                    + "      <table align=\"center\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);\">"
                    + "        <tr>"
                    + "          <td style=\"background-color: #007bff; color: #ffffff; padding: 20px; text-align: center;\">"
                    + "            <h1 style=\"margin: 0; font-size: 24px; font-weight: bold;\">축하합니다! 낙찰에 성공하셨습니다!</h1>"
                    + "          </td>"
                    + "        </tr>"
                    + "        <tr>"
                    + "          <td style=\"padding: 30px;\">"
                    + "            <p style=\"color: #333333; font-size: 16px; line-height: 1.6;\">"
                    + "              안녕하세요,<br><br>"
                    + "              회원님의 입찰이 성공적으로 낙찰되었습니다! 아래 링크를 통해 결제 정보를 확인하고, 다음 단계를 진행해 주세요."
                    + "            </p>"
                    + "            <div style=\"text-align: center; margin: 30px 0;\">"
                    + "              <a href=\"http://localhost:8244/orders" + "\" "
                    + "                 style=\"padding: 12px 24px; background-color: #28a745; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 6px; display: inline-block; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2); transition: background-color 0.3s;\">"
                    + "                결제하기"
                    + "              </a>"
                    + "            </div>"
                    + "            <p style=\"color: #555555; font-size: 14px; line-height: 1.6;\">"
                    + "              감사합니다. 항상 저희 서비스를 이용해 주셔서 감사드리며, 앞으로도 더 좋은 서비스를 제공할 수 있도록 노력하겠습니다."
                    + "            </p>"
                    + "          </td>"
                    + "        </tr>"
                    + "        <tr>"
                    + "          <td style=\"background-color: #f3f4f6; padding: 20px; text-align: center; font-size: 12px; color: #888888;\">"
                    + "            © 2024 Treasuredigger. All rights reserved."
                    + "          </td>"
                    + "        </tr>"
                    + "      </table>"
                    + "    </td>"
                    + "  </tr>"
                    + "</table>"
                    + "</body>"
                    + "</html>";
            emailService.sendEmail(bidDto.getEmail(),"축하드립니다 !! 낙찰성공 안내 메시지 " ,htmlContent);

        };
    }

}
