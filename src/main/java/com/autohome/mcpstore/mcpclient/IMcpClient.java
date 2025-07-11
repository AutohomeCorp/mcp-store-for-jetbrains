package com.autohome.mcpstore.mcpclient;

import java.util.List;

import com.autohome.mcpstore.models.McpServerConfig;
import com.autohome.mcpstore.models.Result;

public interface IMcpClient {
    Boolean isInstalled();

    Result installMcpServer(String name, McpServerConfig config);

    Result uninstallMcpServer(String name);

    List<String> getInstalledMcpServer();
}
