package com.autohome.mcpstore.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.autohome.mcpstore.services.McpStoreSideWindowService;
import com.autohome.mcpstore.services.NotificationService;
import com.intellij.openapi.project.Project;

public class McpStoreSettingChangeListener implements PropertyChangeListener {

    private McpStoreSideWindowService mcpStoreSideWindowService;
    private NotificationService notificationService;

    public McpStoreSettingChangeListener(Project project) {
        this.mcpStoreSideWindowService = project.getService(McpStoreSideWindowService.class);
        this.notificationService = project.getService(NotificationService.class);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("mcpstore.apiHost".equals(evt.getPropertyName())) {
            try {
                mcpStoreSideWindowService.getMcpStoreSideWindow().refreshApiHost((String) evt.getNewValue());
            } catch (Exception e) {
                this.notificationService.error("refreshApiHost failed", e.getMessage());
            }
        }
    }
}