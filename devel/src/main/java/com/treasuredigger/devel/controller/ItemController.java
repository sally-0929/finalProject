package com.treasuredigger.devel.controller;

import com.treasuredigger.devel.constant.ItemStatus;
import com.treasuredigger.devel.entity.Item;
import com.treasuredigger.devel.repository.ItemRepository;
import com.treasuredigger.devel.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.management.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/saveItem")
    public void saveItem(Item item) {
        item.setItemName("test");
        item.setItemDesc("testabout");
        item.setCid("T");
        item.setMaxPrice(3300L);
        item.setItemStatus(ItemStatus.WAIT);
        itemService.saveItem(item.getCid(), item);
    }
}
