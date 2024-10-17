package com.treasuredigger.devel.service;

import com.treasuredigger.devel.comm.GeneratedKey;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private GeneratedKey generatedKey;

    public void saveItem(String categoryId, Item item){

        String itemkey = generatedKey.itemKey(categoryId);
        item.setItemId(itemkey);

        itemRepository.save(item);

    }
}
