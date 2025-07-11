package com.autohome.mcpstore.dao;

import java.util.List;

import com.autohome.mcpstore.models.McpServer;
import com.autohome.mcpstore.models.McpServerDetail;
import com.autohome.mcpstore.models.McpStoreConfig;
import com.autohome.mcpstore.models.Protocol;
import com.autohome.mcpstore.models.QueryServerListParam;
import com.autohome.mcpstore.models.ReportMetric;
import com.autohome.mcpstore.models.ServerListResponse;
import com.autohome.mcpstore.models.ServerSource;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface McpStoreApi {
    @RequestLine("GET /v1/store/server/detail?_appId={_appId}&name={name}")
    Protocol<McpServerDetail> getServerDetail(@Param("_appId") String appId, @Param("name") String name);

    @RequestLine("POST /v1/store/server/list?_appId={_appId}")
    @Headers("Content-Type: application/json")
    Protocol<ServerListResponse<McpServer>> getServerList(@Param("_appId") String appId, QueryServerListParam request);

    @RequestLine("GET /v1/store/server/sources?_appId={_appId}")
    Protocol<List<ServerSource>> getServerSources(@Param("_appId") String appId);

    @RequestLine("POST /v1/store/report?_appId={_appId}")
    @Headers("Content-Type: application/json")
    Protocol<Void> reportMetrics(@Param("_appId") String appId, ReportMetric request);

    @RequestLine("GET /v1/store/config?_appId={_appId}")
    Protocol<McpStoreConfig> getStoreConfig(@Param("_appId") String appId);
}
