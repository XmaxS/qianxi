package com.horizon.search.pojo;

import com.horizon.common.vo.PageResult;
import com.horizon.item.pojo.Brand;
import com.horizon.item.pojo.Category;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SearchResult<Goods> extends PageResult<Goods> {
    private List<Category> categories; //分类待选项
    private List<Brand> brands; //品牌待选项
    private List<Map<String,Object>> specs; //规格参数 key及待选项


    public SearchResult() {
    }

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Category> categories, List<Brand> brands) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
    }

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SearchResult<?> that = (SearchResult<?>) o;
        return Objects.equals(brands, that.brands) &&
                Objects.equals(categories, that.categories) &&
                Objects.equals(specs, that.specs);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), brands, categories, specs);
    }

}
