package com.autohome.mcpstore.toolwindow;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JPanel;

import com.autohome.mcpstore.actions.IssueAction;
import com.autohome.mcpstore.actions.RefreshPage;
import com.autohome.mcpstore.actions.SettingAction;
import com.autohome.mcpstore.services.McpStoreSideWindowService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.jcef.JBCefApp;
import org.jetbrains.annotations.NotNull;

public class McpStoreToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Enable CEF remote debugging
        System.setProperty("ide.browser.jcef.debug.port", "9222");
        System.setProperty("ide.browser.jcef.debug.enabled", "true");
        System.setProperty("ide.browser.jcef.contextMenu.devTools.enabled", "true");
        toolWindow.setTitle("MCP Store");
        ContentManager contentManager = toolWindow.getContentManager();
        JPanel webPanel = new JPanel(new BorderLayout());
        List<AnAction> actionList = new ArrayList<>();

        actionList.add(new SettingAction());
        actionList.add(new IssueAction(project));
        McpStoreSideWindowService service = project.getService(McpStoreSideWindowService.class);
        if (Objects.nonNull(service.getMcpStoreSideWindow()) && Objects.nonNull(service.getMcpStoreSideWindow().getComponent())) {
            actionList.add(new RefreshPage(service.getMcpStoreSideWindow()));
            webPanel.add(service.getMcpStoreSideWindow().getComponent());
            Content labelContent = contentManager.getFactory().createContent(webPanel, "", false);
            contentManager.addContent(labelContent);
        }

//        actionList.add(new SettingAction(ChatGPTBundle.message("action.settings")));

        toolWindow.setTitleActions(actionList);

    }

    static {
        JBCefApp.getInstance();
    }
}
