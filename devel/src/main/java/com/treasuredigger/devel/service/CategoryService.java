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

    public void update(CategoryDto categoryDto){
        ItemCategory itemCategory  = dtoToEntity(categoryDto);
        categoryRepository.findById(itemCategory.getCid()).ifPresent(category -> {
            category.setCname(itemCategory.getCname());
            category.setCDesc(itemCategory.getCDesc());
        });
        categoryRepository.save(itemCategory);

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
        category.setCDesc(categoryDto.getCDesc());
        return category;
    }

    public CategoryDto entityToDto(ItemCategory category) {
        if (category == null) {
            return null;
        }
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCid(category.getCid());
        categoryDto.setCname(category.getCname());
        categoryDto.setCDesc(category.getCDesc());
        return categoryDto;
    }

}
