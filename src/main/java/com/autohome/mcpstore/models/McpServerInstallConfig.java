package com.autohome.mcpstore.models;

import java.util.List;

public class McpServerInstallConfig {
    private String mcpName;
    private List<String> mcpClients;

    // Getters and Setters
    public String getMcpName() {
        return mcpName;
    }

    public void setMcpName(String mcpName) {
        this.mcpName = mcpName;
    }

    public List<String> getMcpClients() {
        return mcpClients;
    }

    public void setMcpClients(List<String> mcpClients) {
        this.mcpClients = mcpClients;
    }
}
