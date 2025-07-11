package com.autohome.mcpstore.models;

import java.util.Collection;
import java.util.List;

public class ServerListResponse<T> extends ProtocolPager<T> {
    private List<ServerCategory> serverCategories;

    public ServerListResponse() {
    }

    public ServerListResponse(Collection<T> list, int pageSize, int pageIndex, int rowCount) {
        super(list, pageSize, pageIndex, rowCount);
    }

    public List<ServerCategory> getServerCategories() {
        return serverCategories;
    }

    public void setServerCategories(List<ServerCategory> serverCategories) {
        this.serverCategories = serverCategories;
    }
}
