package com.autohome.mcpstore.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.autohome.mcpstore.dao.McpStoreApi;
import com.autohome.mcpstore.models.McpServer;
import com.autohome.mcpstore.models.McpServerConfig;
import com.autohome.mcpstore.models.McpServerDetail;
import com.autohome.mcpstore.models.McpStoreConfig;
import com.autohome.mcpstore.models.Protocol;
import com.autohome.mcpstore.models.QueryServerListParam;
import com.autohome.mcpstore.models.ReportMetric;
import com.autohome.mcpstore.models.ServerListResponse;
import com.autohome.mcpstore.models.ServerSource;
import com.autohome.mcpstore.settings.McpStoreSettingsState;
import com.autohome.mcpstore.utils.Constant;
import com.autohome.mcpstore.utils.JacksonHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.intellij.openapi.project.Project;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.opentelemetry.api.internal.StringUtils;


public class McpStoreService {
    private McpStoreApi mcpStoreApi;
    private NotificationService notificationService;

    public McpStoreService(Project project) {
        this.notificationService = project.getService(NotificationService.class);
        String mcpStoreApiUrl = McpStoreSettingsState.getInstance().getApiHost();
        if (mcpStoreApiUrl != null && !mcpStoreApiUrl.isEmpty()) {
            this.mcpStoreApi = create(mcpStoreApiUrl);
        }
    }

    public McpStoreApi getMcpStoreApi() {
        return mcpStoreApi;
    }

    public void setMcpStoreApi(String host) {
        if (host == null || host.isEmpty()) {
            return;
        }
        this.mcpStoreApi = create(host);
    }

    private McpStoreApi create(String host) {
        return feign.Feign.builder()
                .decoder(new JacksonDecoder(JacksonHelper.getCommonObjectMapper()))
                .encoder(new JacksonEncoder(JacksonHelper.getCommonObjectMapper()))
                .target(McpStoreApi.class, host);
    }

    public McpStoreConfig getMcpStoreConfig() {
        try {
            Protocol<McpStoreConfig> protocol = mcpStoreApi.getStoreConfig(Constant.APP_ID);
            if (protocol != null && protocol.getReturncode() == Constant.PROTOCOL_RETURNCODE_SUCCESS) {
                return protocol.getResult();
            } else {
                notificationService.error("getMcpStoreConfig error", protocol.getMessage());
                return null;
            }
        } catch (Exception e) {
            String message = "Failed to get server sources" + e.getMessage();
            notificationService.error("getServerSources error", message);
            return null;
        }
    }

    public Protocol<List<ServerSource>> getServerSources() {
        try {
            Protocol<List<ServerSource>> result = mcpStoreApi.getServerSources(Constant.APP_ID);
            return result;
        } catch (Exception e) {
            String message = "Failed to get server sources" + e.getMessage();
            notificationService.error("getServerSources error", message);
            return new Protocol<>(Constant.PROTOCOL_RETURNCODE_FAIL, message);
        }
    }

    public Protocol<ServerListResponse<McpServer>> getServerList(QueryServerListParam request) {
        try {
            Protocol<ServerListResponse<McpServer>> result = mcpStoreApi.getServerList(Constant.APP_ID, request);
            return result;
        } catch (Exception e) {
            String message = "Failed to getServerList" + e.getMessage();
            notificationService.error("getServerList error", message);
            return new Protocol<>(Constant.PROTOCOL_RETURNCODE_FAIL, message);
        }
    }

    public void reportMetrics(ReportMetric request) {
        try {
            mcpStoreApi.reportMetrics(Constant.APP_ID, request);
        } catch (Exception e) {
            String message = "Failed to reportMetrics" + e.getMessage();
            notificationService.error("reportMetrics error", message);
        }
    }

    public McpServerConfig getMcpServerConfig(String name) {
        try {
            Protocol<McpServerDetail> protocol = mcpStoreApi.getServerDetail(Constant.APP_ID, name);
            if (protocol != null && protocol.getReturncode() == Constant.PROTOCOL_RETURNCODE_SUCCESS && protocol.getResult() != null
                    && !StringUtils.isNullOrEmpty(protocol.getResult().getServerConfig())) {

                HashMap<String, HashMap<String, McpServerConfig>> data = JacksonHelper.deSerialize(protocol.getResult().getServerConfig(), new TypeReference<HashMap<String, HashMap<String, McpServerConfig>>>() {
                });
                if (data == null || !data.containsKey("mcpServers") || data.get("mcpServers") == null || data.get("mcpServers").size() == 0) {
                    notificationService.error("MCP Server Config error", "No mcpServers found in server config");
                    return null;
                }
                HashMap<String, McpServerConfig> mcpServers = data.get("mcpServers");
                Set<Map.Entry<String, McpServerConfig>> entrySet = mcpServers.entrySet();
                McpServerConfig result = entrySet.stream().findFirst().map(Map.Entry::getValue).orElse(null);
                return result;
                //获取map
            } else {
                notificationService.error("getMcpServerConfig error", JacksonHelper.serialize(protocol));
            }
        } catch (Exception e) {
            String message = "Failed to getMcpServerConfig" + e.getMessage();
            notificationService.error("getMcpServerConfig error", message);
        }
        return null;
    }
}
