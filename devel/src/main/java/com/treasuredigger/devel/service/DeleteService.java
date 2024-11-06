package com.treasuredigger.devel.service;

import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteService {

    private final ItemRepository itemRepository;

    public boolean deleteItem(String mid){

        List<Item> items = itemRepository.findBySellerMid(mid);  // 판매자 mid로 판매한 상품 찾기
        boolean allDeleted = true;

        for (Item item : items) {
            itemRepository.delete(item);  // 상품 자체를 삭제

            if (itemRepository.existsById(item.getId())) {
                allDeleted = false; // 삭제 실패한 경우
            }
        }

        return allDeleted; // 모든 항목이 삭제되었는지 여부 반환
    }
}
