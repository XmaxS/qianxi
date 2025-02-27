package com.horizon.item.api;

import com.horizon.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("category")
public interface CategoryApi {

    @GetMapping("list/ids")
    List<Category> queryByIds(@RequestParam("ids") List<Long> ids);
}
