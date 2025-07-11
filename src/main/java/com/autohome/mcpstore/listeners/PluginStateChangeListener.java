package com.autohome.mcpstore.listeners;

import java.util.List;

import com.autohome.mcpstore.services.McpStoreSideWindowService;
import com.autohome.mcpstore.services.NotificationService;
import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class PluginStateChangeListener implements DynamicPluginListener {
    private McpStoreSideWindowService mcpStoreSideWindowService;
    private NotificationService notificationService;
    private List<String> mcpClientListPrevious;


    public PluginStateChangeListener(Project project) {
        this.mcpStoreSideWindowService = project.getService(McpStoreSideWindowService.class);
        this.notificationService = project.getService(NotificationService.class);
        this.mcpClientListPrevious = this.mcpStoreSideWindowService.getMcpStoreSideWindow().getMcpClientList();
    }

    @Override
    public void pluginLoaded(@NotNull IdeaPluginDescriptor plugin) {
        this.notificationService.debug("plugin loaded event", "Plugin " + plugin.getName() + " loaded");

        List<String> list = this.mcpStoreSideWindowService.getMcpStoreSideWindow().getMcpClientList();
        if (hasChanged(this.mcpClientListPrevious, list)) {
            this.mcpClientListPrevious = list;
            this.mcpStoreSideWindowService.getMcpStoreSideWindow().refreshMcpClientList(list);
        }


    }

    @Override
    public void pluginUnloaded(@NotNull IdeaPluginDescriptor plugin, boolean isUpdate) {
        this.notificationService.debug("plugin unloaded event", "Plugin " + plugin.getName() + " unloaded");
        List<String> list = this.mcpStoreSideWindowService.getMcpStoreSideWindow().getMcpClientList();
        if (hasChanged(this.mcpClientListPrevious, list)) {
            this.mcpClientListPrevious = list;
            this.mcpStoreSideWindowService.getMcpStoreSideWindow().refreshMcpClientList(list);
        }
    }

    private boolean hasChanged(List<String> previous, List<String> current) {
        if (previous == current) return false;

        if (previous == null || current == null) {
            return previous != current;
        }
        if (previous.size() != current.size()) {
            return true;
        }
        return !previous.containsAll(current);
    }
}
