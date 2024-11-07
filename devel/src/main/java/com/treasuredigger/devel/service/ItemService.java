package com.treasuredigger.devel.service;

import com.treasuredigger.devel.constant.ItemSellStatus;
import com.treasuredigger.devel.dto.*;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.entity.ItemCategory;
import com.treasuredigger.devel.entity.ItemImg;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.repository.CategoryRepository;
import com.treasuredigger.devel.repository.ItemImgRepository;
import com.treasuredigger.devel.repository.ItemRepository;
import com.treasuredigger.devel.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemImgService itemImgService;

    private final ItemImgRepository itemImgRepository;

    private final CategoryRepository itemCategoryRepository;

    private final MemberRepository memberRepository;


    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        // 카테고리 가져오기
        ItemCategory itemCategory = itemCategoryRepository.findById(itemFormDto.getCid())
                .orElseThrow(EntityNotFoundException::new); // 카테고리 ID 추가

        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserMid = authentication.getName(); // 현재 로그인한 사용자의 mid (이메일 혹은 ID)

        // Member 객체 찾기
        Member currentMember = memberRepository.findByMid(currentUserMid);
        if (currentMember == null) {
            throw new EntityNotFoundException("Member not found");
        }

        //상품 등록
        Item item = itemFormDto.createItem(itemCategory);
        itemRepository.save(item);

        // 아이템에 판매자 설정
        item.setSeller(currentMember);
        itemRepository.save(item);

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if(i == 0)
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i), false);
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        if (item.getItemCategory() != null) {
            itemFormDto.setCid(item.getItemCategory().getCid());
            itemFormDto.setCname(item.getItemCategory().getCname());
        }

        Member seller = item.getSeller();
        itemFormDto.setSellerId(seller.getMid());
        itemFormDto.setSellerRole(seller.getMemberGrade().getMemberGradeStatus().name());

        return itemFormDto;
    }

    @Transactional(readOnly = true)
    public List<ItemFormDto> getItemsBySeller(Member seller, Pageable pageable) {
        List<Item> items = itemRepository.findBySeller(seller, pageable);
        List<ItemFormDto> itemFormDtos = new ArrayList<>();
        for (Item item : items) {
            itemFormDtos.add(getItemDtl(item.getId()));
        } return itemFormDtos;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);

        // 카테고리 업데이트
        ItemCategory itemCategory = itemCategoryRepository.findById(itemFormDto.getCid())
                .orElseThrow(EntityNotFoundException::new); // 카테고리 ID 추가
        item.setItemCategory(itemCategory); // 카테고리 설정

        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i), false);
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

    public ItemSellStatus getItemSellStatus(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        return item.getItemSellStatus();
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPageByCategory(String cid, ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPageByCategory(cid, itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ItemDto> getAdminItemDtos(Pageable pageable, ItemSearchDto itemSearchDto) {
        Page<Item> items = itemRepository.getAdminItemPage(itemSearchDto, pageable); // ItemSearchDto와 Pageable을 사용
        return items.map(item -> {
            ItemDto itemDto = new ItemDto();
            itemDto.setId(item.getId());
            itemDto.setItemNm(item.getItemNm());
            itemDto.setPrice(item.getPrice());
            itemDto.setItemDetail(item.getItemDetail());
            itemDto.setRegTime(item.getRegTime());
            itemDto.setUpdateTime(item.getUpdateTime());
            itemDto.setCname(item.getItemCategory().getCname());
            itemDto.setStockNumber(item.getStockNumber());
            itemDto.setItemSellStatus(item.getItemSellStatus());

            ItemImg repImg = itemImgRepository.findByItemIdAndRepimgYn(item.getId(), "Y");
            if (repImg != null) {
                itemDto.setImgUrl(repImg.getImgUrl());
            }

            return itemDto;
        });
    }




}