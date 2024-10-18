//package com.treasuredigger.devel.comm;
//
//
//import com.treasuredigger.devel.repository.ItemRepository;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.Query;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//@Component
//public class GeneratedKey {
//
//    @Autowired
//    ItemRepository itemRepository;
//
//
//    public String itemKey(String categoryId) {
//        String prefix = new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(new Date()) + categoryId;
//        long count = itemRepository.countByIdStartingWith(prefix);
//
//        return prefix + String.format("%03d", count + 1);
//    }
//
//}
//
//
//
