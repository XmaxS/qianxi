package com.horizon.search.pojo;

//页面请求对象
public class SearchRequest {
    private String key; //搜索字段

    private Integer page; //当前页

    private static final int DEFAULT_SIZE = 20; //页面大小
    private static final int DEFAULT_PAGE = 1; //默认页

    public String getKey(){
        return key;
    }

    public void setKey(){
        this.key = key;
    }

    public Integer getPage(){
        if (page == null){
            return DEFAULT_PAGE;
        }
        //获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE,page);
    }

    public void setPage(Integer page){
        this.page = page;
    }

    public Integer getSize(){
        return DEFAULT_SIZE;
    }
}
