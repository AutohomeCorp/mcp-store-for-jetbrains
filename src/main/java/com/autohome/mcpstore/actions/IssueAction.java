package com.autohome.mcpstore.actions;

import com.autohome.mcpstore.models.McpStoreConfig;
import com.autohome.mcpstore.services.McpStoreService;
import com.autohome.mcpstore.services.NotificationService;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ReflectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class IssueAction extends AnAction {

    private McpStoreService mcpStoreService;
    private NotificationService notificationService;

    public IssueAction(Project project) {
        super(() -> "New Issue", IconLoader.getIcon("icons/issue_16x16.png", ReflectionUtil.getGrandCallerClass()));
        this.mcpStoreService = project.getService(McpStoreService.class);
        this.notificationService = project.getService(NotificationService.class);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            McpStoreConfig mcpStoreConfig = mcpStoreService.getMcpStoreConfig();
            if (mcpStoreConfig != null) {
                String issueUrl = mcpStoreConfig.getIssueUrl();
                if (StringUtils.isNotEmpty(issueUrl)) {
                    BrowserUtil.browse(issueUrl);
                }
            }
        } catch (Exception ex) {
            notificationService.error("IssueAction error", ex.getMessage());
        }
    }

}
