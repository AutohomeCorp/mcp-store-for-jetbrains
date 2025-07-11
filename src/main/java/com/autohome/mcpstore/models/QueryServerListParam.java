package com.autohome.mcpstore.models;

import java.util.List;

public class QueryServerListParam {
    private int pageSize;
    private int pageIndex;
    private String keyword;
    private Integer serverCategoryId;
    private Integer source;
    private List<String> names;
    private Integer namesFilterType;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getServerCategoryId() {
        return serverCategoryId;
    }

    public void setServerCategoryId(Integer serverCategoryId) {
        this.serverCategoryId = serverCategoryId;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public Integer getNamesFilterType() {
        return namesFilterType;
    }

    public void setNamesFilterType(Integer namesFilterType) {
        this.namesFilterType = namesFilterType;
    }
}
