package com.treasuredigger.devel.service;


import com.treasuredigger.devel.dto.CategoryDto;
import com.treasuredigger.devel.entity.ItemCategory;
import com.treasuredigger.devel.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    final private CategoryRepository categoryRepository;



    public List<ItemCategory> list(){

        return categoryRepository.findAll();


    }


    public void save(CategoryDto categoryDto){
        ItemCategory itemCategory  = dtoToEntity(categoryDto);
        categoryRepository.save(itemCategory);

    }

    public void update(CategoryDto categoryDto) {
        System.out.println("update data ++ " + categoryDto);
        ItemCategory itemCategory = dtoToEntity(categoryDto);
        categoryRepository.findById(itemCategory.getCid()).ifPresent(existingCategory -> {
            existingCategory.setCname(itemCategory.getCname());
            existingCategory.setCdesc(itemCategory.getCdesc()); // Cdesc 수정
            categoryRepository.save(existingCategory); // 수정된 기존 엔티티를 저장
        });
    }


    public void delete(String cid){
        categoryRepository.deleteById(cid);
    }

     public ItemCategory dtoToEntity(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        ItemCategory category = new ItemCategory();
        category.setCid(categoryDto.getCid());
        category.setCname(categoryDto.getCname());
        category.setCdesc(categoryDto.getCdesc());
        return category;
    }

    public CategoryDto entityToDto(ItemCategory category) {
        if (category == null) {
            return null;
        }
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCid(category.getCid());
        categoryDto.setCname(category.getCname());
        categoryDto.setCdesc(category.getCdesc());
        return categoryDto;
    }

}
