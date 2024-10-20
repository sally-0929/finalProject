package com.treasuredigger.devel.service;


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

}
