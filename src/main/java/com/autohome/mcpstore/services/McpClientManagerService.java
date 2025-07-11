package com.autohome.mcpstore.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.autohome.mcpstore.enums.CodeEnum;
import com.autohome.mcpstore.enums.McpClientEnum;
import com.autohome.mcpstore.mcpclient.IMcpClient;
import com.autohome.mcpstore.mcpclient.impl.LingmaMcpClient;
import com.autohome.mcpstore.models.McpServerConfig;
import com.autohome.mcpstore.models.McpServerInstallConfig;
import com.autohome.mcpstore.models.ReportMetric;
import com.autohome.mcpstore.models.Result;
import com.autohome.mcpstore.utils.SystemUtil;
import com.intellij.openapi.project.Project;

public class McpClientManagerService {
    private final Map<String, IMcpClient> servers;
    private NotificationService notificationService;
    private McpStoreService mcpStoreService;

    public McpClientManagerService(Project project) {
        this.servers = new HashMap<>();
        this.notificationService = project.getService(NotificationService.class);
        mcpStoreService = project.getService(McpStoreService.class);
        // 初始化默认客户端
        servers.put(McpClientEnum.LINGMA.getName(), new LingmaMcpClient(project));

    }

    public void addServer(String name, IMcpClient server) {
        servers.put(name, server);
    }

    public IMcpClient getServer(String name) {
        return servers.get(name);
    }

    public Result installMcpServer(McpServerInstallConfig config) {
        Integer code = CodeEnum.SUCCESS.getValue();
        StringBuffer message = new StringBuffer();

        McpServerConfig mcpServerConfig = mcpStoreService.getMcpServerConfig(config.getMcpName());
        if (mcpServerConfig == null) {
            return new Result(1, "get McpServerConfig failed");
        }
        for (String client : config.getMcpClients()) {
            IMcpClient server = getServer(client);
            if (server == null) {
                continue;
            }
            Result result = server.installMcpServer(config.getMcpName(), mcpServerConfig);
            if (result.getCode() != CodeEnum.SUCCESS.getValue()) {
                String currentMessage = client + ": " + result.getMessage();
                if (code == CodeEnum.SUCCESS.getValue()) {//首次安装失败
                    code = CodeEnum.ERROR.getValue();
                    message.append("install " + config.getMcpName() + " failed ").append("\r\n");
                    message.append(currentMessage).append("\r\n");
                } else {
                    message.append(", ").append(currentMessage).append("\r\n");
                }
            } else {
                message.append(client + ": " + "install " + config.getMcpName() + " success ").append("\r\n");
                this.reportMetrics(config.getMcpName(), client, "install");
            }
        }
        Result result = new Result(code, message.toString());
        notificationService.info("install mcp server", result.getMessage());
        return result;
    }

    public Result uninstallMcpServer(McpServerInstallConfig config) {
        Integer code = CodeEnum.SUCCESS.getValue();
        StringBuffer message = new StringBuffer();
        for (String client : config.getMcpClients()) {
            IMcpClient server = getServer(client);
            if (server == null) {
                continue;
            }
            Result result = server.uninstallMcpServer(config.getMcpName());
            if (result.getCode() != CodeEnum.SUCCESS.getValue()) {
                String currentMessage = client + ":" + result.getMessage();
                if (code == CodeEnum.SUCCESS.getValue()) {//首次安装失败
                    code = CodeEnum.ERROR.getValue();
                    message.append("uninstall " + config.getMcpName() + " failed").append("\r\n");
                    message.append(currentMessage).append("\r\n");
                } else {
                    message.append(", ").append(currentMessage).append("\r\n");
                }
            } else {
                message.append(client + ": " + "uninstall " + config.getMcpName() + " success ").append("\r\n");
                this.reportMetrics(config.getMcpName(), client, "uninstall");
            }
        }
        Result result = new Result(code, message.toString());
        notificationService.info("uninstall mcp server", result.getMessage());
        return result;
    }

    public List<String> getInstalledMcpClients() {
        ArrayList installedMcpClients = new ArrayList<>();
        for (String name : servers.keySet()) {
            if (servers.get(name).isInstalled()) {
                installedMcpClients.add(name);
            }
        }
        return installedMcpClients;
    }

    public Map<String, List<String>> getInstalledMcpServerList() {
        Map<String, List<String>> result = new HashMap<>();
        servers.forEach((name, client) -> {
            result.put(name, client.getInstalledMcpServer());
        });
        return result;
    }

    public void reportMetrics(String mcpServerName, String target, String eventName) {
        ReportMetric metric = new ReportMetric();
        metric.setMcpServerName(mcpServerName);
        metric.setTarget(target);
        metric.setEventName(eventName);
        metric.setEventTime(new Date());
        metric.setUsername(SystemUtil.getLoginUserName());
        metric.setUserIp(SystemUtil.getLocalHostIP());
        metric.setUserOs(SystemUtil.getOs().getValue());
        metric.setSource(SystemUtil.getIdeName());

        mcpStoreService.reportMetrics(metric);
    }
}