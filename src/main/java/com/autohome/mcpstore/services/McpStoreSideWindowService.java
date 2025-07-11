package com.autohome.mcpstore.services;

import com.autohome.mcpstore.toolwindow.McpStoreSideWindow;
import com.intellij.openapi.project.Project;

public class McpStoreSideWindowService {

    private final McpStoreSideWindow mcpStoreSideWindow;

    public McpStoreSideWindowService(Project project) {
        this.mcpStoreSideWindow = new McpStoreSideWindow(project);
    }

    public McpStoreSideWindow getMcpStoreSideWindow() {
        return this.mcpStoreSideWindow;
    }

}
