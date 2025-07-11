package com.autohome.mcpstore.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;


public class ProtocolPager<T> implements Serializable {
    private static final long serialVersionUID = 8938161129268809414L;
    private int rowcount;
    private int pagecount;
    private int pageindex;
    private Collection<T> list;

    public ProtocolPager() {
    }

    public ProtocolPager(Collection list, int pageSize, int pageIndex, int rowCount) {
        this.list = list;
        this.rowcount = rowCount;
        this.pagecount = getPageCount(rowCount, pageSize);
        this.pageindex = pageIndex;
    }


    public int getRowcount() {
        return rowcount;
    }

    public void setRowcount(int rowcount) {
        this.rowcount = rowcount;
    }

    public int getPagecount() {
        return pagecount;
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

    public int getPageindex() {
        return pageindex;
    }

    public void setPageindex(int pageindex) {
        this.pageindex = pageindex;
    }

    public Collection<T> getList() {
        //和调用方约定后，过滤list中的null元素
        if (list != null && list.contains(null)) {
            list = list.stream().filter(l -> l != null).collect(Collectors.toList());
        }
        return list;
    }

    public void setList(Collection<T> list) {
        this.list = list;
    }

    private int getPageCount(int rowCount, int pageSize) {
        int tempPageSize = pageSize;
        if (tempPageSize <= 0) {
            tempPageSize = 10;
        }
        return rowCount % tempPageSize == 0 ? rowCount / tempPageSize : (rowCount / tempPageSize) + 1;
    }
}
