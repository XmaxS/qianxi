package com.horizon.web;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.horizon.common.Item;
import com.horizon.common.enums.ExceptionEnums;
import com.horizon.common.exception.QxException;
import com.horizon.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping
    public ResponseEntity<Item> saveItem(Item item){

        if (item.getPrice() == null){
            throw new QxException(ExceptionEnums.PRICE_CANNOT_BE_NULL);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.saveItem(item));
    }
}
