package com.treasuredigger.devel.comm;


import com.treasuredigger.devel.repository.BidItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
@Component
public class GeneratedKey {

    @Autowired
    BidItemRepository bidItemRepository;


    public String itemKey(String categoryId) {
        String prefix = new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(new Date()) + categoryId;
        long count = bidItemRepository.countByBidItemIdStartingWith(prefix);

        return prefix + String.format("%03d", count + 1);
    }

}



